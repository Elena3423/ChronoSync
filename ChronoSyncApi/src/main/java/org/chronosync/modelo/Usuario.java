package org.chronosync.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;

    @Column(name = "apellidos")
    private String apellidos;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Column(nullable = false)
    private Boolean activo = false;

    @ManyToOne
    @JoinColumn(name = "rol_id")
    private Rol rol;

    @ManyToOne
    @JoinColumn(name = "negocio_id")
    private Negocio negocio;
}
