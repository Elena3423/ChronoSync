package org.chronosync.servicio;

import lombok.RequiredArgsConstructor;
import org.chronosync.modelo.Rol;
import org.chronosync.repositorio.RolRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RolServicio {
    private final RolRepo rolRepo;

    public List<Rol> ListarRoles() {
        return rolRepo.findAll();
    }

    public Rol obtener(Integer id) {
        return rolRepo.findById(id).orElseThrow(() -> new RuntimeException("Rol no encontrado"));
    }
}
