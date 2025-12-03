package org.chronosync.servicio;

import lombok.RequiredArgsConstructor;
import org.chronosync.modelo.Negocio;
import org.chronosync.repositorio.NegocioRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NegocioServicio {

    private final NegocioRepo negocioRepo;

    public Negocio crear(Negocio negocio){
        negocio.setCodigoUnion(UUID.randomUUID().toString().substring(0,8));
        return negocioRepo.save(negocio);
    }

    public List<Negocio> listar(){
        return negocioRepo.findAll();
    }

    public Negocio obtener(Integer id){
        return negocioRepo.findById(id).orElseThrow(() -> new RuntimeException("Negocio no encontrado"));
    }
}
