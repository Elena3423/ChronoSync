package org.chronosync.repositorio;

import org.chronosync.modelo.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepo extends JpaRepository<Rol, Integer> {
}
