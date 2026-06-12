package com.proyecto.proyectoncapas.services.reservation.impl;

import com.proyecto.proyectoncapas.dto.request.ReservationRequestDTO;
import com.proyecto.proyectoncapas.dto.response.*;
import com.proyecto.proyectoncapas.entities.*;
import com.proyecto.proyectoncapas.exception.InvalidPaymentStateException;
import com.proyecto.proyectoncapas.exception.InvalidReservationException;
import com.proyecto.proyectoncapas.exception.ReservationNotFoundException;
import com.proyecto.proyectoncapas.exception.ResourceNotFoundException;
import com.proyecto.proyectoncapas.repository.*;
import com.proyecto.proyectoncapas.services.payment.PaymentService;
import com.proyecto.proyectoncapas.services.reservation.BookingContext;
import com.proyecto.proyectoncapas.services.reservation.ReservationService;
import com.proyecto.proyectoncapas.utils.enums.ExtensionRequestStatus;
import com.proyecto.proyectoncapas.utils.enums.ReservationStatus;
import com.proyecto.proyectoncapas.utils.enums.RuleType;
import com.proyecto.proyectoncapas.utils.mappers.ReservationMapper;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {

    private static final List<ExtensionRequestStatus> ACTIVE_EXTENSION_STATUSES = List.of(
            ExtensionRequestStatus.PENDING,
            ExtensionRequestStatus.APPROVED
    );

    private final PropertyRepository propertyRepository;
    private final ReservationRepository reservationRepository;
    private final PropertyAvailabilityRepository availabilityRepository;
    private final PriceCalculationService priceCalculationService;
    private final UserRepository userRepository;
    private final ContractRepository contractRepository;
    private final ExtensionRequestRepository extensionRequestRepository;
    private final PaymentService paymentService;

    public ReservationServiceImpl(
            PropertyRepository propertyRepository,
            ReservationRepository reservationRepository,
            PropertyAvailabilityRepository availabilityRepository,
            PriceCalculationService priceCalculationService,
            UserRepository userRepository,
            ContractRepository contractRepository,
            ExtensionRequestRepository extensionRequestRepository,
            @Lazy PaymentService paymentService) {
        this.propertyRepository = propertyRepository;
        this.reservationRepository = reservationRepository;
        this.availabilityRepository = availabilityRepository;
        this.priceCalculationService = priceCalculationService;
        this.userRepository = userRepository;
        this.contractRepository = contractRepository;
        this.extensionRequestRepository = extensionRequestRepository;
        this.paymentService = paymentService;
    }

    @Override
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

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad no encontrada"));

        LocalDate endOfOccupation = checkOut.minusDays(1);

        long occupiedDaysCount = availabilityRepository.countOccupiedDaysForUpdate(propertyId, checkIn, endOfOccupation);
        if (occupiedDaysCount > 0) {
            throw new InvalidReservationException("La propiedad ya no está disponible en las fechas seleccionadas.");
        }

        List<PropertyRule> rulesToApply = getPropertyRules(property).stream()
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

        for (LocalDate date = checkIn; !date.isAfter(endOfOccupation); date = date.plusDays(1)) {
            PropertyAvailability availability = new PropertyAvailability();
            availability.setProperty(property);
            availability.setDate(date);
            availability.setReservation(savedBooking);
            availabilityRepository.save(availability);
        }

        return ReservationMapper.toResponseDTO(savedBooking);
    }

    @Override
    public void cancelOrReleaseExpiredBooking(Long bookingId) {
        Reservation booking = reservationRepository.findById(bookingId).orElse(null);
        if (booking != null && ReservationStatus.PENDING_PAYMENT.equals(booking.getStatus())) {
            booking.setStatus(ReservationStatus.EXPIRED);
            reservationRepository.save(booking);
            availabilityRepository.deleteByReservationId(bookingId);
        }
    }

    @Override
    @Transactional
    public CancellationQuoteResponseDTO quoteCancellation(Long reservationId) {
        Reservation booking = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reserva no encontrada"));

        Property property = booking.getProperty();
        LocalDate today = LocalDate.now();

        int totalNights = (int) ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        BookingContext context = new BookingContext(
                totalNights,
                property.getPricePerNight(),
                booking.getNumberOfGuests() != null ? booking.getNumberOfGuests() : 1,
                booking.getCheckInDate(),
                today,
                false,
                0
        );

        BigDecimal priceWithPenalties = priceCalculationService.calculateFinalPrice(property, context);
        BigDecimal baseTotalPrice = property.getPricePerNight().multiply(BigDecimal.valueOf(totalNights));
        BigDecimal penaltyFee = priceWithPenalties.subtract(baseTotalPrice).max(BigDecimal.ZERO);
        BigDecimal refundAmount = booking.getTotalAmount().subtract(penaltyFee).max(BigDecimal.ZERO);

        return new CancellationQuoteResponseDTO(
                reservationId,
                booking.getTotalAmount(),
                penaltyFee,
                refundAmount
        );
    }

    @Override
    public Reservation findById(Long id) {
        return reservationRepository.findById(id).orElseThrow(() -> new ReservationNotFoundException("No existe el registro"));
    }

    @Override
    @Transactional
    public ExtensionQuoteResponseDTO quoteExtension(Long id, int extraDays) {
        Reservation originalBooking = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));
        validateExtensionEligible(originalBooking);
        return buildExtensionQuote(originalBooking, extraDays);
    }

    @Override
    @Transactional
    public ExtensionRequestResponseDTO requestExtension(Long reservationId, int extraDays) {
        if (extraDays <= 0) {
            throw new InvalidReservationException("Extra days must be greater than zero");
        }

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reserva no encontrada"));
        validateExtensionEligible(reservation);

        if (extensionRequestRepository.existsByReservationIdAndStatusIn(reservationId, ACTIVE_EXTENSION_STATUSES)) {
            throw new InvalidReservationException("There is already an active extension request for this reservation");
        }

        ExtensionQuoteResponseDTO quote = buildExtensionQuote(reservation, extraDays);

        ExtensionRequest request = ExtensionRequest.builder()
                .reservation(reservation)
                .extraDays(extraDays)
                .quotedAmount(quote.getExtensionSubtotal())
                .status(ExtensionRequestStatus.PENDING)
                .build();

        request = extensionRequestRepository.save(request);
        return toExtensionRequestDTO(request);
    }

    @Override
    @Transactional
    public ExtensionRequestResponseDTO approveExtension(Long requestId, Long landlordId) {
        ExtensionRequest request = extensionRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Extension request not found"));

        if (!ExtensionRequestStatus.PENDING.equals(request.getStatus())) {
            throw new InvalidReservationException("Extension request is not pending approval");
        }

        validateLandlordOwnership(request.getReservation(), landlordId);

        User landlord = userRepository.findById(landlordId)
                .orElseThrow(() -> new ResourceNotFoundException("Landlord not found"));

        request.setStatus(ExtensionRequestStatus.APPROVED);
        request.setResolvedAt(LocalDateTime.now());
        request.setResolvedBy(landlord);

        return toExtensionRequestDTO(extensionRequestRepository.save(request));
    }

    @Override
    @Transactional
    public ExtensionRequestResponseDTO rejectExtension(Long requestId, Long landlordId) {
        ExtensionRequest request = extensionRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Extension request not found"));

        if (!ExtensionRequestStatus.PENDING.equals(request.getStatus())) {
            throw new InvalidReservationException("Extension request is not pending approval");
        }

        validateLandlordOwnership(request.getReservation(), landlordId);

        User landlord = userRepository.findById(landlordId)
                .orElseThrow(() -> new ResourceNotFoundException("Landlord not found"));

        request.setStatus(ExtensionRequestStatus.REJECTED);
        request.setResolvedAt(LocalDateTime.now());
        request.setResolvedBy(landlord);

        return toExtensionRequestDTO(extensionRequestRepository.save(request));
    }

    @Override
    @Transactional
    public BookingExtensionResponseDTO applyApprovedExtension(Long extensionRequestId, BigDecimal paidAmount) {
        ExtensionRequest request = extensionRequestRepository.findByIdAndStatus(extensionRequestId, ExtensionRequestStatus.APPROVED)
                .orElseThrow(() -> new InvalidPaymentStateException("Extension request not found or not approved"));

        if (paidAmount.compareTo(request.getQuotedAmount()) != 0) {
            throw new InvalidPaymentStateException("Paid amount does not match quoted extension amount");
        }

        Reservation originalBooking = request.getReservation();
        int extraDays = request.getExtraDays();
        Property property = originalBooking.getProperty();

        LocalDate extensionStart = originalBooking.getCheckOutDate();
        LocalDate extensionEnd = extensionStart.plusDays(extraDays);
        LocalDate endOfOccupation = extensionEnd.minusDays(1);

        long occupiedDaysCount = availabilityRepository.countOccupiedDaysForUpdate(property.getId(), extensionStart, endOfOccupation);
        if (occupiedDaysCount > 0) {
            throw new InvalidReservationException("Los días para la extensión ya no están disponibles.");
        }

        originalBooking.setCheckOutDate(extensionEnd);
        originalBooking.setTotalAmount(originalBooking.getTotalAmount().add(paidAmount));
        originalBooking.setIsExtended(true);
        reservationRepository.save(originalBooking);

        for (LocalDate date = extensionStart; !date.isAfter(endOfOccupation); date = date.plusDays(1)) {
            PropertyAvailability availability = new PropertyAvailability();
            availability.setProperty(property);
            availability.setDate(date);
            availability.setReservation(originalBooking);
            availabilityRepository.save(availability);
        }

        request.setStatus(ExtensionRequestStatus.PAID);
        request.setResolvedAt(LocalDateTime.now());
        extensionRequestRepository.save(request);

        return new BookingExtensionResponseDTO(
                originalBooking.getId(),
                originalBooking.getCheckInDate(),
                originalBooking.getCheckOutDate(),
                paidAmount,
                originalBooking.getTotalAmount()
        );
    }

    @Override
    @Transactional
    public CancellationResponseDTO confirmCancellation(Long reservationId) {
        Reservation booking = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reserva no encontrada"));

        if (booking.getStatus().equals(ReservationStatus.CANCELLED)) {
            throw new InvalidReservationException("La reserva ya se encuentra cancelada.");
        }

        if (!ReservationStatus.CONFIRMED.equals(booking.getStatus())
                && !ReservationStatus.CHECKED_IN.equals(booking.getStatus())) {
            throw new InvalidReservationException("Only confirmed reservations can be cancelled with refund");
        }

        CancellationQuoteResponseDTO quote = this.quoteCancellation(reservationId);

        if (quote.getRefundAmount().compareTo(BigDecimal.ZERO) > 0) {
            paymentService.processPartialRefund(reservationId, quote.getRefundAmount());
        }

        booking.setStatus(ReservationStatus.CANCELLED);
        booking.setCancellationDate(LocalDate.now());
        booking.setTotalAmount(quote.getPenaltyFee());
        reservationRepository.save(booking);

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

        List<PropertyRule> rulesToApply = getPropertyRules(property).stream()
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

    @Override
    public List<ExtensionRequestResponseDTO> getExtensionRequestsByReservation(Long reservationId) {
        reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + reservationId));

        return extensionRequestRepository.findByReservationIdOrderByRequestedAtDesc(reservationId).stream()
                .map(this::toExtensionRequestDTO)
                .toList();
    }

    @Override
    public List<ExtensionRequestLandlordResponseDTO> getLandlordExtensionRequests(Long landlordId, String status) {
        ExtensionRequestStatus statusFilter = null;
        if (status != null && !status.isBlank()) {
            try {
                statusFilter = ExtensionRequestStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidReservationException("Invalid extension request status: " + status);
            }
        }

        return extensionRequestRepository.findByLandlordIdAndOptionalStatus(landlordId, statusFilter).stream()
                .map(this::toLandlordExtensionRequestDTO)
                .toList();
    }

    private ExtensionQuoteResponseDTO buildExtensionQuote(Reservation originalBooking, int extraDays) {
        Property property = originalBooking.getProperty();

        BookingContext extensionContext = new BookingContext(
                extraDays,
                property.getPricePerNight(),
                originalBooking.getNumberOfGuests() != null ? originalBooking.getNumberOfGuests() : 1,
                originalBooking.getCheckInDate(),
                null,
                true,
                extraDays
        );

        List<PropertyRule> rulesToApply = getPropertyRules(property).stream()
                .filter(rule -> rule.getRuleType() != RuleType.CLEANING_FEE && rule.getRuleType() != RuleType.INSURANCE_FEE)
                .toList();

        BigDecimal extensionSubtotal = priceCalculationService.calculateFinalPriceWithCustomRules(rulesToApply, extensionContext);

        BigDecimal baseAmount = property.getPricePerNight().multiply(BigDecimal.valueOf(extraDays));

        BigDecimal extensionFeePerNight = BigDecimal.ZERO;
        for (PropertyRule rule : getPropertyRules(property)) {
            if (rule.getRuleType() == RuleType.STAY_EXTENSION_FEE) {
                extensionFeePerNight = rule.getValue();
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

        return new ExtensionQuoteResponseDTO(
                originalBooking.getId(),
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

    private void validateExtensionEligible(Reservation reservation) {
        if (!ReservationStatus.CONFIRMED.equals(reservation.getStatus())
                && !ReservationStatus.CHECKED_IN.equals(reservation.getStatus())) {
            throw new InvalidReservationException("Only confirmed reservations can be extended");
        }
    }

    private void validateLandlordOwnership(Reservation reservation, Long landlordId) {
        if (!reservation.getProperty().getLandlord().getId().equals(landlordId)) {
            throw new InvalidReservationException("Only the property landlord can resolve extension requests");
        }
    }

    private List<PropertyRule> getPropertyRules(Property property) {
        return property.getRules() != null ? property.getRules() : Collections.emptyList();
    }

    private ExtensionRequestResponseDTO toExtensionRequestDTO(ExtensionRequest request) {
        return ExtensionRequestResponseDTO.builder()
                .id(request.getId())
                .reservationId(request.getReservation().getId())
                .extraDays(request.getExtraDays())
                .quotedAmount(request.getQuotedAmount())
                .status(request.getStatus().name())
                .requestedAt(request.getRequestedAt())
                .resolvedAt(request.getResolvedAt())
                .resolvedById(request.getResolvedBy() != null ? request.getResolvedBy().getId() : null)
                .build();
    }

    private ExtensionRequestLandlordResponseDTO toLandlordExtensionRequestDTO(ExtensionRequest request) {
        Reservation reservation = request.getReservation();
        Property property = reservation.getProperty();
        String tenantName = reservation.getTenant() != null ? reservation.getTenant().getFullName() : "Inquilino";

        return ExtensionRequestLandlordResponseDTO.builder()
                .id(request.getId())
                .reservationId(reservation.getId())
                .extraDays(request.getExtraDays())
                .quotedAmount(request.getQuotedAmount())
                .status(request.getStatus().name())
                .requestedAt(request.getRequestedAt())
                .resolvedAt(request.getResolvedAt())
                .resolvedById(request.getResolvedBy() != null ? request.getResolvedBy().getId() : null)
                .propertyTitle(property.getTitle())
                .propertyCity(property.getCity())
                .tenantName(tenantName)
                .currentCheckOutDate(reservation.getCheckOutDate())
                .build();
    }
}
