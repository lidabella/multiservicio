package com.multiservicio.util;

import com.multiservicio.entity.Usuario;
import com.multiservicio.exception.AuthException;
import com.multiservicio.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UsuarioRepository usuarioRepository;

    public Usuario getUsuarioActual() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsernameAndActivoTrue(username)
                .orElseThrow(() -> new AuthException("Usuario no autenticado", HttpStatus.UNAUTHORIZED));
    }
}
