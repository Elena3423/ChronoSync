package org.chronosync.repositorio;

import org.chronosync.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepo extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);
}
