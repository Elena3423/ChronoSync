package org.chronosync.servicio;

import lombok.RequiredArgsConstructor;
import org.chronosync.modelo.Negocio;
import org.chronosync.modelo.Rol;
import org.chronosync.modelo.Usuario;
import org.chronosync.repositorio.NegocioRepo;
import org.chronosync.repositorio.RolRepo;
import org.chronosync.repositorio.UsuarioRepo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServicio {
    private final UsuarioRepo usuarioRepo;
    private final RolRepo rolRepo;
    private final NegocioRepo negocioRepo;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Usuario registrar(Usuario usuario, Integer rolId){
        if (usuarioRepo.findByEmail(usuario.getEmail()).isPresent()){
            throw new RuntimeException("Ya existe un usuario con ese email");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        Rol rol = rolRepo.findById(rolId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado."));

        usuario.setRol(rol);
        usuario.setActivo(false);

        return usuarioRepo.save(usuario);
    }

    public Usuario login(String email, String password) {
        Usuario usuario = usuarioRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        if (!passwordEncoder.matches(password, usuario.getPassword())){
            throw new RuntimeException("Contraseña incorrecta.");
        }

        return usuario;
    }

    public void activarUsuario(Integer id) {
        Usuario usuario = usuarioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
        usuario.setActivo(true);
        usuarioRepo.save(usuario);
    }

    public Usuario unirseANegocio(Integer usuarioId, String codigoUnion) {
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        Negocio negocio = negocioRepo.findByCodigoUnion(codigoUnion)
                .orElseThrow(() -> new RuntimeException("Código de unión inválido."));

        usuario.setNegocio(negocio);
        return usuarioRepo.save(usuario);
    }
}
