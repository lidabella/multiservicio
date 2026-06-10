package com.multiservicio.service;

import com.multiservicio.dto.AuthResponse;
import com.multiservicio.dto.LoginRequest;
import com.multiservicio.dto.RegisterRequest;
import com.multiservicio.entity.Rol;
import com.multiservicio.entity.Usuario;
import com.multiservicio.exception.AuthException;
import com.multiservicio.repository.RolRepository;
import com.multiservicio.repository.UsuarioRepository;
import com.multiservicio.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse registrar(RegisterRequest request) {

        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new AuthException("El username ya existe", HttpStatus.CONFLICT);
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()
                && usuarioRepository.existsByEmail(request.getEmail())) {
            throw new AuthException("El email ya existe", HttpStatus.CONFLICT);
        }

        Rol rol = resolverRol(request.getRolId());

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setRol(rol);

        Usuario guardado = usuarioRepository.save(usuario);
        String token = jwtService.generateToken(guardado.getUsername(), guardado.getRol().getNombre());

        return AuthResponse.builder()
                .success(true)
                .message("Usuario registrado correctamente")
                .username(guardado.getUsername())
                .nombre(guardado.getNombre())
                .rol(guardado.getRol().getNombre())
                .token(token)
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {

        Usuario usuario = usuarioRepository.findByUsernameAndActivoTrue(request.getUsername())
                .orElseThrow(() -> new AuthException("Usuario no encontrado", HttpStatus.UNAUTHORIZED));

        if (!passwordMatches(request.getPassword(), usuario.getPassword())) {
            throw new AuthException("Contraseña incorrecta", HttpStatus.UNAUTHORIZED);
        }

        String token = jwtService.generateToken(usuario.getUsername(), usuario.getRol().getNombre());

        return AuthResponse.builder()
                .success(true)
                .message("Bienvenido " + usuario.getNombre())
                .username(usuario.getUsername())
                .nombre(usuario.getNombre())
                .rol(usuario.getRol().getNombre())
                .token(token)
                .build();
    }

    private boolean passwordMatches(String raw, String stored) {
        if (stored.startsWith("$2a$") || stored.startsWith("$2b$")) {
            return passwordEncoder.matches(raw, stored);
        }
        return stored.equals(raw);
    }

    private Rol resolverRol(Long rolId) {
        if (rolId != null) {
            return rolRepository.findById(rolId)
                    .orElseThrow(() -> new AuthException("Rol no encontrado", HttpStatus.BAD_REQUEST));
        }
        return rolRepository.findByNombre("OPERADOR")
                .orElseThrow(() -> new AuthException("Rol OPERADOR no configurado", HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
