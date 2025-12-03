package org.chronosync.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "exportacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exportacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_formato", nullable = false)
    private TipoExportacion tipoFormato;

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDateTime fechaGeneracion;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocio negocio;
}