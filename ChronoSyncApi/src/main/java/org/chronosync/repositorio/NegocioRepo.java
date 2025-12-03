package org.chronosync.repositorio;

import org.chronosync.modelo.Negocio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NegocioRepo extends JpaRepository<Negocio, Integer> {
    Optional<Negocio> findByCodigoUnion(String codigoUnion);
}
