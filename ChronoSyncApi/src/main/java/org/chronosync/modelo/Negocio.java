package org.chronosync.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "negocio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Negocio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
    private String direccion;
    private String telefono;

    @Column(unique = true)
    private String email;

    @Column(name = "codigo_union")
    private String codigoUnion;
}
