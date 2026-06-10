package com.multiservicio.config;

import com.multiservicio.entity.Rol;
import com.multiservicio.entity.Usuario;
import com.multiservicio.repository.RolRepository;
import com.multiservicio.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Rol rolAdmin = crearRolSiNoExiste("ADMIN");
        Rol rolOperador = crearRolSiNoExiste("OPERADOR");
        Rol rolRecepcion = crearRolSiNoExiste("RECEPCION");

        crearUsuarioSiNoExiste("admin", "Administrador", "Sistema",
                "admin@multiservicio.com", "1234", rolAdmin);
        crearUsuarioSiNoExiste("operador", "Operador", "Ventanilla",
                "operador@multiservicio.com", "1234", rolOperador);
        crearUsuarioSiNoExiste("recepcion", "Recepcion", "Turnos",
                "recepcion@multiservicio.com", "1234", rolRecepcion);

        migrarContrasenasLegacy();
    }

    private Rol crearRolSiNoExiste(String nombre) {
        return rolRepository.findByNombre(nombre)
                .orElseGet(() -> {
                    Rol rol = new Rol();
                    rol.setNombre(nombre);
                    return rolRepository.save(rol);
                });
    }

    private void crearUsuarioSiNoExiste(String username, String nombre, String apellido,
                                        String email, String password, Rol rol) {
        if (!usuarioRepository.existsByUsername(username)) {
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setUsername(username);
            usuario.setEmail(email);
            usuario.setPassword(passwordEncoder.encode(password));
            usuario.setActivo(true);
            usuario.setFechaCreacion(LocalDateTime.now());
            usuario.setRol(rol);
            usuarioRepository.save(usuario);
        }
    }

    private void migrarContrasenasLegacy() {
        usuarioRepository.findAll().forEach(usuario -> {
            String pwd = usuario.getPassword();
            if (pwd != null && !pwd.startsWith("$2a$") && !pwd.startsWith("$2b$")) {
                usuario.setPassword(passwordEncoder.encode(pwd));
                usuarioRepository.save(usuario);
            }
        });
    }
}
