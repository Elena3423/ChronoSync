package org.chronosync.repositorio;

import org.chronosync.modelo.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurnoRepo extends JpaRepository<Turno, Integer> {
}
