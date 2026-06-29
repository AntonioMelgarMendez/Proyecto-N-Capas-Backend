package com.proyecto.proyectoncapas.entities;


import com.proyecto.proyectoncapas.utils.enums.FineStatus;
import com.proyecto.proyectoncapas.utils.enums.InfractionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fines")
public class Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fine_id")
    private Long fineId;

    // Relación Many-to-one con Usuarios (este será el infractor)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reciever"))
    private User user;

    // Relación Many-to-one con Usuarios (este será el Admin/Arrendador)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_by", nullable = false, foreignKey = @ForeignKey(name = "fk_transmitter"))
    private User generatedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "infraction_type")
    private InfractionType infractionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "fine_state")
    private FineStatus status;

    @Column(name = "amount", nullable = false, precision = 9, scale = 2)
    private BigDecimal amount;

    @Column(name = "description")
    private String description;

    @Column(name = "infraction_date")
    private LocalDateTime infractionDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) this.status = FineStatus.PENDING;
        if (this.infractionDate == null) this.infractionDate = LocalDateTime.now();
    }
}
