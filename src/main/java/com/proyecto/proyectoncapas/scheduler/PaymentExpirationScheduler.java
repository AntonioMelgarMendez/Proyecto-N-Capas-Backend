package com.proyecto.proyectoncapas.scheduler;

import com.proyecto.proyectoncapas.entities.Reservation;
import com.proyecto.proyectoncapas.repository.ReservationRepository;
import com.proyecto.proyectoncapas.services.payment.PaymentService;
import com.proyecto.proyectoncapas.utils.enums.ReservationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentExpirationScheduler {

    private final ReservationRepository reservationRepository;
    private final PaymentService paymentService;

    @Value("${payment.reservation-expiration-minutes:30}")
    private int expirationMinutes;

    @Scheduled(fixedRate = 900000)
    public void expirePendingPaymentReservations() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(expirationMinutes);
        List<Reservation> expiredReservations = reservationRepository.findByStatusAndCreatedAtBefore(
                ReservationStatus.PENDING_PAYMENT,
                cutoff
        );

        for (Reservation reservation : expiredReservations) {
            try {
                paymentService.handleCheckoutCancelled(reservation.getId());
                log.info("Expired pending payment reservation {}", reservation.getId());
            } catch (Exception e) {
                log.error("Failed to expire reservation {}", reservation.getId(), e);
            }
        }
    }
}
