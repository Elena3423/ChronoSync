package org.chronosync.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "turno")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTurno tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTurno estado;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
