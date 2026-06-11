package com.proyecto.proyectoncapas.services.reservation.impl;

import com.proyecto.proyectoncapas.dto.request.ReservationRequestDTO;
import com.proyecto.proyectoncapas.dto.response.*;
import com.proyecto.proyectoncapas.entities.Property;
import com.proyecto.proyectoncapas.entities.PropertyAvailability;
import com.proyecto.proyectoncapas.entities.PropertyRule;
import com.proyecto.proyectoncapas.entities.Reservation;
import com.proyecto.proyectoncapas.exception.InvalidReservationException;
import com.proyecto.proyectoncapas.exception.ReservationNotFoundException;
import com.proyecto.proyectoncapas.exception.ResourceNotFoundException;
import com.proyecto.proyectoncapas.repository.PropertyAvailabilityRepository;
import com.proyecto.proyectoncapas.repository.PropertyRepository;
import com.proyecto.proyectoncapas.repository.ReservationRepository;
import com.proyecto.proyectoncapas.repository.UserRepository;
import com.proyecto.proyectoncapas.repository.ContractRepository;
import com.proyecto.proyectoncapas.entities.User;
import com.proyecto.proyectoncapas.entities.Contract;
import com.proyecto.proyectoncapas.entities.PropertyPhoto;
import com.proyecto.proyectoncapas.services.reservation.BookingContext;
import com.proyecto.proyectoncapas.services.reservation.ReservationService;
import com.proyecto.proyectoncapas.utils.enums.ReservationStatus;
import com.proyecto.proyectoncapas.utils.enums.RuleType;
import com.proyecto.proyectoncapas.utils.mappers.ReservationMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@AllArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final PropertyRepository propertyRepository;
    private final ReservationRepository reservationRepository;
    private final PropertyAvailabilityRepository availabilityRepository;
    private final PriceCalculationService priceCalculationService;
    private final UserRepository userRepository;
    private final ContractRepository contractRepository;

    @Transactional
    public ReservationResponseDTO createBooking(Long propertyId, ReservationRequestDTO request) {
        LocalDate checkIn = request.getCheckInDate();
        LocalDate checkOut = request.getCheckOutDate();
        if (!checkOut.isAfter(checkIn)) {
            throw new InvalidReservationException("La fecha de salida debe ser posterior a la de entrada");
        }

        if (checkIn.isBefore(LocalDate.now())) {
            throw new InvalidReservationException("La fecha de entrada debe ser de ahora en adelante");
        }

        // 1. Obtener la propiedad
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad no encontrada"));

        // El rango de reserva excluye el día exacto de check-out (Check-out es a la mañana, entra otro huésped)
        LocalDate endOfOccupation = checkOut.minusDays(1);

        // 2. CONCURRENCIA CONTROLADA: Validar si los días están libres usando bloqueo pesimista
        long occupiedDaysCount = availabilityRepository.countOccupiedDaysForUpdate(propertyId, checkIn, endOfOccupation);
        if (occupiedDaysCount > 0) {
            throw new InvalidReservationException("La propiedad ya no está disponible en las fechas seleccionadas.");
        }

        List<PropertyRule> rulesToApply = property.getRules().stream()
                .filter(rule -> {
                    if (rule.getRuleType() == RuleType.CLEANING_FEE && !request.isIncludeCleaning()) {
                        return false; // El usuario desmarcó la limpieza
                    }
                    if (rule.getRuleType() == RuleType.INSURANCE_FEE && !request.isIncludeInsurance()) {
                        return false; // El usuario no quiso el seguro
                    }
                    return true; // Descuentos por larga estadía u otras reglas fijas siempre pasan
                })
                .toList();

        // 3. Preparar contexto y ejecutar tu motor de cálculo de precios
        int totalNights = (int) ChronoUnit.DAYS.between(checkIn, checkOut);
        BookingContext context = new BookingContext(
                totalNights,
                property.getPricePerNight(),
                request.getNumberOfGuests(),
                checkIn,
                null,  // No es cancelación
                false, // No es extensión
                0
        );

        BigDecimal finalPrice = priceCalculationService.calculateFinalPriceWithCustomRules(rulesToApply, context);

        // 4. Persistir la Reserva
        Reservation booking = new Reservation();
        booking.setProperty(property);
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        booking.setNumberOfGuests(request.getNumberOfGuests());
        booking.setTotalAmount(finalPrice);
        booking.setStatus(ReservationStatus.PENDING_PAYMENT);

        User tenant = userRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with ID: " + request.getTenantId()));
        booking.setTenant(tenant);

        Reservation savedBooking = reservationRepository.save(booking);

        // 5. Bloquear atómicamente los días en la tabla de disponibilidad
        for (LocalDate date = checkIn; !date.isAfter(endOfOccupation); date = date.plusDays(1)) {
            PropertyAvailability availability = new PropertyAvailability();
            availability.setProperty(property);
            availability.setDate(date);
            availability.setReservation(savedBooking);
            availabilityRepository.save(availability);
        }

        return ReservationMapper.toResponseDTO(savedBooking);
    }

    public void cancelOrReleaseExpiredBooking(Long bookingId) {
        Reservation booking = reservationRepository.findById(bookingId).orElse(null);
        if (booking != null && ReservationStatus.PENDING_PAYMENT.equals(booking.getStatus())) {

            // 1. Cambiar estado a cancelado/expirado
            booking.setStatus(ReservationStatus.EXPIRED);
            reservationRepository.save(booking);

            // 2. Liberar el calendario inmediatamente
            availabilityRepository.deleteByReservationId(bookingId);
        }
    }

    @Transactional
    public CancellationQuoteResponseDTO quoteCancellation(Long reservationId) {
        Reservation booking = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reserva no encontrada"));

        Property property = booking.getProperty();
        LocalDate today = LocalDate.now();

        // Construimos el contexto con la fecha de cancelación activa
        int totalNights = (int) ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        BookingContext context = new BookingContext(
                totalNights,
                property.getPricePerNight(),
                booking.getNumberOfGuests() != null ? booking.getNumberOfGuests() : 1, // <-- CORRECCIÓN: Null-safe fallback
                booking.getCheckInDate(),
                today,                       // Activamos la lógica de cancelación tardía proporcional
                false,
                0
        );
        // El motor calcula: Precio Base + Penalizaciones aplicables (si aplica)
        BigDecimal priceWithPenalties = priceCalculationService.calculateFinalPrice(property, context);

        // La penalización real cobrada es la diferencia entre el cálculo con penalización y el costo base original
        BigDecimal baseTotalPrice = property.getPricePerNight().multiply(BigDecimal.valueOf(totalNights));
        BigDecimal penaltyFee = priceWithPenalties.subtract(baseTotalPrice).max(BigDecimal.ZERO);

        // El reembolso es lo que pagó originalmente el cliente menos la penalización aplicada
        BigDecimal refundAmount = booking.getTotalAmount().subtract(penaltyFee).max(BigDecimal.ZERO);

        return new CancellationQuoteResponseDTO(
                reservationId,
                booking.getTotalAmount(), // lo que pagó originalmente
                penaltyFee,              // la multa por cancelar tarde
                refundAmount             // lo que se le va a devolver a su tarjeta
        );
    }

    @Override
    public Reservation findById(Long id) {
        return reservationRepository.findById(id).orElseThrow(() -> new ReservationNotFoundException("No existe el registro"));
    }

    @Transactional
    public BookingExtensionResponseDTO processExtensionPayment(Long reservationId, int extraDays) {
        // 1. Recuperar la reserva original
        Reservation originalBooking = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reserva no encontrada"));

        Property property = originalBooking.getProperty();

        // 2. Calcular los nuevos rangos de fechas
        // Si el check-out original era el 15, la extensión empieza el 15 y termina el 15 + extraDays
        LocalDate extensionStart = originalBooking.getCheckOutDate();
        LocalDate extensionEnd = extensionStart.plusDays(extraDays);
        LocalDate endOfOccupation = extensionEnd.minusDays(1); // Excluyendo el día de salida

        // 3. CONCURRENCIA: Bloquear y verificar si los días de la extensión están disponibles
        long occupiedDaysCount = availabilityRepository.countOccupiedDaysForUpdate(property.getId(), extensionStart, endOfOccupation);
        if (occupiedDaysCount > 0) {
            throw new InvalidReservationException("Los días para la extensión ya no están disponibles.");
        }

        // 4. Calcular el precio final idéntico al quote (filtrando limpieza y seguro)
        List<PropertyRule> rulesToApply = property.getRules().stream()
                .filter(rule -> rule.getRuleType() != RuleType.CLEANING_FEE && rule.getRuleType() != RuleType.INSURANCE_FEE)
                .toList();

        BookingContext extensionContext = new BookingContext(
                extraDays,
                property.getPricePerNight(),
                originalBooking.getNumberOfGuests() != null ? originalBooking.getNumberOfGuests() : 1,   // <-- CORRECCIÓN: Null-safe fallback
                originalBooking.getCheckInDate(),
                null,
                true,
                extraDays
        );
        BigDecimal totalToPay = priceCalculationService.calculateFinalPriceWithCustomRules(rulesToApply, extensionContext);

        // 5. [AQUÍ INTEGRAS TU PASARELA] Simular o procesar el pago real
        // paymentService.charge(userId, totalToPay);

        // 6. PERSISTENCIA: Actualizar la reserva original (Mover el Check-out)
        originalBooking.setCheckOutDate(extensionEnd);
        originalBooking.setTotalAmount(originalBooking.getTotalAmount().add(totalToPay)); // Sumamos al histórico total
        reservationRepository.save(originalBooking);

        // 7. Bloquear atómicamente los nuevos días en el calendario de disponibilidad
        for (LocalDate date = extensionStart; !date.isAfter(endOfOccupation); date = date.plusDays(1)) {
            PropertyAvailability availability = new PropertyAvailability();
            availability.setProperty(property);
            availability.setDate(date);
            availability.setReservation(originalBooking); // Ligado a la misma reserva
            availabilityRepository.save(availability);
        }

        // 8. Retornar el DTO de confirmación
        return new BookingExtensionResponseDTO(
                originalBooking.getId(),
                originalBooking.getCheckInDate(),
                originalBooking.getCheckOutDate(), // Nueva fecha extendida
                totalToPay,                        // Lo que pagó justo ahora
                originalBooking.getTotalAmount()    // Total acumulado histórico
        );
    }


    @Transactional
    public ExtensionQuoteResponseDTO quoteExtension(Long id, int extraDays) {

        // 1. Recuperar la reserva original ya pagada
        Reservation originalBooking = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        Property property = originalBooking.getProperty();

        // 2. Construir el contexto enfocado SOLO en los días extra
        BookingContext extensionContext = new BookingContext(
                extraDays,
                property.getPricePerNight(),
                originalBooking.getNumberOfGuests() != null ? originalBooking.getNumberOfGuests() : 1,
                originalBooking.getCheckInDate(),
                null,
                true,
                extraDays
        );

        // 3. El motor calcula el costo de la extensión con las reglas de la propiedad (filtrando limpieza y seguro)
        List<PropertyRule> rulesToApply = property.getRules().stream()
                .filter(rule -> rule.getRuleType() != RuleType.CLEANING_FEE && rule.getRuleType() != RuleType.INSURANCE_FEE)
                .toList();
        BigDecimal extensionSubtotal = priceCalculationService.calculateFinalPriceWithCustomRules(rulesToApply, extensionContext);

        // 4. Calcular el desglose
        BigDecimal baseAmount = property.getPricePerNight().multiply(BigDecimal.valueOf(extraDays));
        
        BigDecimal extensionFeePerNight = BigDecimal.ZERO;
        if (property.getRules() != null) {
            for (PropertyRule rule : property.getRules()) {
                if (rule.getRuleType() == RuleType.STAY_EXTENSION_FEE) {
                    extensionFeePerNight = rule.getValue();
                }
            }
        }
        BigDecimal extensionFeeTotal = extensionFeePerNight.multiply(BigDecimal.valueOf(extraDays));

        BigDecimal baseRentPlusFee = baseAmount.add(extensionFeeTotal);
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal surchargeAmount = BigDecimal.ZERO;

        if (baseRentPlusFee.compareTo(extensionSubtotal) > 0) {
            discountAmount = baseRentPlusFee.subtract(extensionSubtotal);
        } else if (extensionSubtotal.compareTo(baseRentPlusFee) > 0) {
            surchargeAmount = extensionSubtotal.subtract(baseRentPlusFee);
        }

        // 5. Preparar la respuesta para el cliente
        return new ExtensionQuoteResponseDTO(
                id,
                extraDays,
                property.getPricePerNight(),
                baseAmount,
                extensionFeePerNight,
                extensionFeeTotal,
                discountAmount,
                surchargeAmount,
                extensionSubtotal
        );
    }

    @Transactional
    public CancellationResponseDTO confirmCancellation(Long reservationId) {
        Reservation booking = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reserva no encontrada"));

        if (booking.getStatus().equals(ReservationStatus.CANCELLED)) {
            throw new InvalidReservationException("La reserva ya se encuentra cancelada.");
        }

        // 1. Calcular la cotización final real al momento del impacto
        CancellationQuoteResponseDTO quote = this.quoteCancellation(reservationId);

        // 2. [AQUÍ INTEGRAS TU PASARELA] Ejecutar el reembolso real de dinero si aplica
        // if (quote.getRefundAmount().compareTo(BigDecimal.ZERO) > 0) {
        //     paymentService.refund(booking.getPaymentId(), quote.getRefundAmount());
        // }

        // 3. PERSISTENCIA: Cambiar el estado de la reserva y actualizar el precio final retenido
        booking.setStatus(ReservationStatus.CANCELLED);
        // El precio total de la reserva pasa a ser únicamente el valor de la penalización que se quedó el negocio
        booking.setTotalAmount(quote.getPenaltyFee());
        reservationRepository.save(booking);

        // 4. LIBERAR EL CALENDARIO: Borramos los bloqueos atómicos de disponibilidad de forma inmediata
        // Esto vacía los días indexados para que vuelvan a estar listos para otros miles de usuarios concurrentes
        availabilityRepository.deleteByReservationId(booking.getId());

        return new CancellationResponseDTO(
                booking.getId(),
                ReservationStatus.CANCELLED.name(),
                quote.getPenaltyFee(),
                quote.getRefundAmount()
        );
    }

    @Override
    @Transactional
    public ReservationQuoteResponseDTO calculateQuote(Long propertyId, ReservationRequestDTO request) {
        LocalDate checkIn = request.getCheckInDate();
        LocalDate checkOut = request.getCheckOutDate();
        if (!checkOut.isAfter(checkIn)) {
            throw new InvalidReservationException("La fecha de salida debe ser posterior a la de entrada");
        }

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad no encontrada"));

        List<PropertyRule> rulesToApply = property.getRules().stream()
                .filter(rule -> {
                    if (rule.getRuleType() == RuleType.CLEANING_FEE && !request.isIncludeCleaning()) {
                        return false;
                    }
                    if (rule.getRuleType() == RuleType.INSURANCE_FEE && !request.isIncludeInsurance()) {
                        return false;
                    }
                    return true;
                })
                .toList();

        int totalNights = (int) ChronoUnit.DAYS.between(checkIn, checkOut);
        BookingContext context = new BookingContext(
                totalNights,
                property.getPricePerNight(),
                request.getNumberOfGuests(),
                checkIn,
                null,
                false,
                0
        );

        BigDecimal finalPrice = priceCalculationService.calculateFinalPriceWithCustomRules(rulesToApply, context);

        return ReservationQuoteResponseDTO.builder()
                .propertyId(propertyId)
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .numberOfGuests(request.getNumberOfGuests())
                .totalNights(totalNights)
                .basePricePerNight(property.getPricePerNight())
                .totalAmount(finalPrice)
                .build();
    }

    @Override
    public List<TenantReservationResponseDTO> getTenantReservations(Long tenantId) {
        List<Reservation> reservations = reservationRepository.findByTenant_IdOrderByCheckInDateDesc(tenantId);
        return reservations.stream()
                .map(r -> {
                    String contractStatus = contractRepository.findByReservationId(r.getId())
                            .map(Contract::getStatus)
                            .map(status -> "SIGNED".equalsIgnoreCase(status) ? "Firmado" : "Pendiente")
                            .orElse("Pendiente");

                    String coverPhoto = null;
                    if (r.getProperty().getPhotos() != null && !r.getProperty().getPhotos().isEmpty()) {
                        coverPhoto = r.getProperty().getPhotos().get(0).getS3Url();
                    }

                    // Deterministic 6-digit access PIN based on reservation id
                    String pin = String.valueOf((r.getId() * 179 + 100000) % 900000 + 100000);

                    return TenantReservationResponseDTO.builder()
                            .id(r.getId())
                            .propertyId(r.getProperty().getId())
                            .propertyTitle(r.getProperty().getTitle())
                            .propertyCity(r.getProperty().getCity())
                            .propertyCoverPhoto(coverPhoto)
                            .checkInDate(r.getCheckInDate())
                            .checkOutDate(r.getCheckOutDate())
                            .numberOfGuests(r.getNumberOfGuests())
                            .totalAmount(r.getTotalAmount())
                            .status(r.getStatus().name())
                            .contractStatus(contractStatus)
                            .pin(pin)
                            .build();
                })
                .toList();
    }
}
