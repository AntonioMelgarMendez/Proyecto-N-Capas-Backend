package com.proyecto.proyectoncapas.controller.reservation;

import com.proyecto.proyectoncapas.dto.request.ReservationRequestDTO;
import com.proyecto.proyectoncapas.dto.response.*;
import com.proyecto.proyectoncapas.entities.Property;
import com.proyecto.proyectoncapas.entities.Reservation;
import com.proyecto.proyectoncapas.services.reservation.AvailabilityService;
import com.proyecto.proyectoncapas.services.reservation.BookingContext;
import com.proyecto.proyectoncapas.services.reservation.ReservationService;
import com.proyecto.proyectoncapas.services.reservation.impl.PriceCalculationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/reservations")
@AllArgsConstructor
public class ReservationController {

    private final AvailabilityService availabilityService;
    private PriceCalculationService priceCalculationService;
    private final ReservationService bookingService;

    @GetMapping("/{id}/calendar")
    public ResponseEntity<GeneralResponse<List<LocalDate>>> getPropertyCalendar(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        List<LocalDate> occupiedDates = availabilityService.getOccupiedCalendar(id, start, end);
        return ResponseEntity.ok(
                GeneralResponse.<List<LocalDate>>builder()
                        .message("Calendar retrieved successfully")
                        .data(occupiedDates)
                        .build()
        );
    }

        @PostMapping("/{id}/book")
        public ResponseEntity<GeneralResponse<ReservationResponseDTO>> bookProperty( @PathVariable Long id,
                @Valid @RequestBody ReservationRequestDTO reservationRequestDTO) {


        ReservationResponseDTO newBooking = bookingService.createBooking(id, reservationRequestDTO);

        return ResponseEntity.ok(
                GeneralResponse.<ReservationResponseDTO>builder()
                        .message("Book Successfully Created")
                        .data(newBooking)
                        .build()
        );

    }

    @PostMapping("/{id}/extend/quote")//TODO: Migrar logica a service.
    public ResponseEntity<GeneralResponse<ExtensionQuoteResponseDTO>> quoteExtension(
            @PathVariable Long id,
            @RequestParam int extraDays) {

        // 1. Recuperar la reserva original ya pagada
        Reservation originalBooking = bookingService.findById(id);

        Property property = originalBooking.getProperty();

        // 2. Construir el contexto enfocado SOLO en los días extra
        // Pasamos 'extraDays' como la estadía base de esta nueva cotización en caliente
        BookingContext extensionContext = new BookingContext(
                extraDays,                          // totalDays para el cálculo base
                property.getPricePerNight(),
                originalBooking.getNumberOfGuests(),
                originalBooking.getCheckInDate(),   // checkIn original
                null,                               // cancellationDate (no aplica)
                true,                               // isExtension = true
                extraDays                           // extendedDays para el recargo
        );

        // 3. El motor calcula el costo de la extensión con las reglas de la propiedad
        BigDecimal extensionSubtotal = priceCalculationService.
                calculateFinalPrice(property, extensionContext);

        // 4. Preparar la respuesta para el cliente
        ExtensionQuoteResponseDTO data = new ExtensionQuoteResponseDTO(
                id,
                extraDays,
                property.getPricePerNight().multiply(BigDecimal.valueOf(extraDays)), // precio base extra
                extensionSubtotal // total con recargos de extensión incluidos
        );

        return ResponseEntity.ok(
                GeneralResponse.<ExtensionQuoteResponseDTO>builder()
                        .message("Extension Information")
                        .data(data)
                        .build()
        );

    }

    @PostMapping("/{id}/extend/pay")
    public ResponseEntity<GeneralResponse<BookingExtensionResponseDTO>> payExtension(
            @PathVariable Long id,
            @RequestParam int extraDays) {

        // Ejecuta la lógica de negocio, validación de disponibilidad, cobro y persistencia
        BookingExtensionResponseDTO data = bookingService.processExtensionPayment(id, extraDays);

        return ResponseEntity.ok(
                GeneralResponse.<BookingExtensionResponseDTO>builder()
                        .message("Extension paid and confirmed successfully")
                        .data(data)
                        .build()
        );
    }


    @PostMapping("/{id}/cancel/quote")
    public ResponseEntity<GeneralResponse<CancellationQuoteResponseDTO>> quoteCancellation(
            @PathVariable Long id) {

        CancellationQuoteResponseDTO data = bookingService.quoteCancellation(id);

        return ResponseEntity.ok(
                GeneralResponse.<CancellationQuoteResponseDTO>builder()
                        .message("Cancellation Quote Information")
                        .data(data)
                        .build()
        );
    }

    @PostMapping("/{id}/cancel/confirm")
    public ResponseEntity<GeneralResponse<CancellationResponseDTO>> confirmCancellation(
            @PathVariable Long id) {

        CancellationResponseDTO data = bookingService.confirmCancellation(id);

        return ResponseEntity.ok(
                GeneralResponse.<CancellationResponseDTO>builder()
                        .message("Reservation cancelled successfully. Calendar days released.")
                        .data(data)
                        .build()
        );
    }
}
