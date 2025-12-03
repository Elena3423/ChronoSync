package org.chronosync.repositorio;

import org.chronosync.modelo.Exportacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExportacionRepo extends JpaRepository<Exportacion, Integer> {
}
