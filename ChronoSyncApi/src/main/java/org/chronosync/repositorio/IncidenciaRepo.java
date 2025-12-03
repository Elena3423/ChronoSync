package org.chronosync.repositorio;

import org.chronosync.modelo.Incidencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidenciaRepo extends JpaRepository<Incidencia, Integer> {
}
