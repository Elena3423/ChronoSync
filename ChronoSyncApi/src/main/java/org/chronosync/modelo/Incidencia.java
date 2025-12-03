package org.chronosync.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "incidencias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Incidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoIncidencia tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoIncidencia estado;

    private String comentarios;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "turno_id", nullable = false)
    private Turno turno;
}
