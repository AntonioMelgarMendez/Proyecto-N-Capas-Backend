package com.proyecto.proyectoncapas.entities;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "reservas")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Relaciones

    private BigDecimal montoTotal;
    private String estadoPago; // Ej: "PENDIENTE", "CONFIRMADO"

    @Column(name = "stripe_session_id")
    private String stripeSessionId;
}