package com.multiservicio.service;

import com.multiservicio.dto.ServicioRequest;
import com.multiservicio.dto.ServicioResponse;
import com.multiservicio.entity.Servicio;
import com.multiservicio.exception.AuthException;
import com.multiservicio.repository.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioService {

    private final ServicioRepository servicioRepository;

    @Transactional(readOnly = true)
    public List<ServicioResponse> listar(Boolean soloActivos) {
        List<Servicio> servicios = Boolean.TRUE.equals(soloActivos)
                ? servicioRepository.findByActivoTrue()
                : servicioRepository.findAll();
        return servicios.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ServicioResponse obtener(Long id) {
        return toResponse(buscar(id));
    }

    @Transactional
    public ServicioResponse crear(ServicioRequest request) {
        if (servicioRepository.existsByNombre(request.getNombre())) {
            throw new AuthException("Ya existe un servicio con ese nombre", HttpStatus.CONFLICT);
        }
        if (servicioRepository.existsByPrefijo(request.getPrefijo())) {
            throw new AuthException("Ya existe un servicio con ese prefijo", HttpStatus.CONFLICT);
        }

        Servicio servicio = new Servicio();
        aplicarDatos(servicio, request);
        servicio.setFechaCreacion(LocalDateTime.now());
        return toResponse(servicioRepository.save(servicio));
    }

    @Transactional
    public ServicioResponse actualizar(Long id, ServicioRequest request) {
        Servicio servicio = buscar(id);
        aplicarDatos(servicio, request);
        return toResponse(servicioRepository.save(servicio));
    }

    public Servicio buscarActivo(Long id) {
        Servicio servicio = buscar(id);
        if (!Boolean.TRUE.equals(servicio.getActivo())) {
            throw new AuthException("El servicio no está activo", HttpStatus.BAD_REQUEST);
        }
        return servicio;
    }

    private Servicio buscar(Long id) {
        return servicioRepository.findById(id)
                .orElseThrow(() -> new AuthException("Servicio no encontrado", HttpStatus.NOT_FOUND));
    }

    private void aplicarDatos(Servicio servicio, ServicioRequest request) {
        servicio.setNombre(request.getNombre());
        servicio.setDescripcion(request.getDescripcion());
        servicio.setPrefijo(request.getPrefijo().toUpperCase());
        servicio.setDuracionEstimada(request.getDuracionEstimada());
        servicio.setActivo(request.getActivo() != null ? request.getActivo() : true);
    }

    private ServicioResponse toResponse(Servicio servicio) {
        return ServicioResponse.builder()
                .id(servicio.getId())
                .nombre(servicio.getNombre())
                .descripcion(servicio.getDescripcion())
                .prefijo(servicio.getPrefijo())
                .duracionEstimada(servicio.getDuracionEstimada())
                .activo(servicio.getActivo())
                .build();
    }
}
