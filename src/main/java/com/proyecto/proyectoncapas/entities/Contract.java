package com.proyecto.proyectoncapas.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "contracts")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    // --- Relaciones Futuras ---
    /*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private User tenant; // El inquilino

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landlord_id", nullable = false)
    private User landlord; // El arrendador
    */

    @Column(columnDefinition = "TEXT")
    private String content;

    // Estados sugeridos -> "DRAFT", "PENDING_SIGNATURE", "SIGNED", "VOIDED"
    @Column(nullable = false)
    private String status;

    @Column(name = "landlord_signature_date")
    private LocalDateTime landlordSignatureDate;

    @Column(name = "tenant_signature_date")
    private LocalDateTime tenantSignatureDate;

    @Column(name = "landlord_signature_url")
    private String landlordSignatureUrl;

    @Column(name = "tenant_signature_url")
    private String tenantSignatureUrl;

    @Column(name = "signature_hash", unique = true, length = 64)
    private String signatureHash;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "DRAFT";
        }
    }
}