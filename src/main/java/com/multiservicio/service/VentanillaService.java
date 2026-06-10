package com.multiservicio.service;

import com.multiservicio.dto.ServicioResponse;
import com.multiservicio.dto.VentanillaRequest;
import com.multiservicio.dto.VentanillaResponse;
import com.multiservicio.entity.Servicio;
import com.multiservicio.entity.Ventanilla;
import com.multiservicio.exception.AuthException;
import com.multiservicio.repository.ServicioRepository;
import com.multiservicio.repository.VentanillaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VentanillaService {

    private final VentanillaRepository ventanillaRepository;
    private final ServicioRepository servicioRepository;

    @Transactional(readOnly = true)
    public List<VentanillaResponse> listar() {
        return ventanillaRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public VentanillaResponse obtener(Long id) {
        return toResponse(buscar(id));
    }

    @Transactional
    public VentanillaResponse crear(VentanillaRequest request) {
        Ventanilla ventanilla = new Ventanilla();
        ventanilla.setFechaCreacion(LocalDateTime.now());
        aplicarDatos(ventanilla, request);
        return toResponse(ventanillaRepository.save(ventanilla));
    }

    @Transactional
    public VentanillaResponse actualizar(Long id, VentanillaRequest request) {
        Ventanilla ventanilla = buscar(id);
        aplicarDatos(ventanilla, request);
        return toResponse(ventanillaRepository.save(ventanilla));
    }

    public Ventanilla buscarDisponibleParaServicio(Long ventanillaId, Long servicioId) {
        Ventanilla ventanilla = buscar(ventanillaId);

        if (!Boolean.TRUE.equals(ventanilla.getActiva())) {
            throw new AuthException("La ventanilla no está activa", HttpStatus.BAD_REQUEST);
        }
        if (ventanilla.getEstadoVentanilla() != com.multiservicio.entity.enums.EstadoVentanilla.DISPONIBLE) {
            throw new AuthException("La ventanilla no está disponible", HttpStatus.BAD_REQUEST);
        }

        boolean atiendeServicio = ventanilla.getServicios().stream()
                .anyMatch(s -> s.getId().equals(servicioId));
        if (!atiendeServicio) {
            throw new AuthException("La ventanilla no atiende este servicio", HttpStatus.BAD_REQUEST);
        }

        return ventanilla;
    }

    private Ventanilla buscar(Long id) {
        return ventanillaRepository.findById(id)
                .orElseThrow(() -> new AuthException("Ventanilla no encontrada", HttpStatus.NOT_FOUND));
    }

    private void aplicarDatos(Ventanilla ventanilla, VentanillaRequest request) {
        ventanilla.setNombre(request.getNombre());
        ventanilla.setUbicacion(request.getUbicacion());
        ventanilla.setActiva(request.getActiva() != null ? request.getActiva() : true);
        if (request.getEstado() != null) {
            ventanilla.setEstadoVentanilla(request.getEstado());
        }

        if (request.getServicioIds() != null) {
            Set<Servicio> servicios = new HashSet<>();
            for (Long servicioId : request.getServicioIds()) {
                Servicio servicio = servicioRepository.findById(servicioId)
                        .orElseThrow(() -> new AuthException("Servicio no encontrado: " + servicioId, HttpStatus.BAD_REQUEST));
                servicios.add(servicio);
            }
            ventanilla.setServicios(servicios);
        }
    }

    private VentanillaResponse toResponse(Ventanilla ventanilla) {
        List<ServicioResponse> servicios = ventanilla.getServicios().stream()
                .map(s -> ServicioResponse.builder()
                        .id(s.getId())
                        .nombre(s.getNombre())
                        .prefijo(s.getPrefijo())
                        .activo(s.getActivo())
                        .build())
                .toList();

        return VentanillaResponse.builder()
                .id(ventanilla.getId())
                .nombre(ventanilla.getNombre())
                .ubicacion(ventanilla.getUbicacion())
                .activa(ventanilla.getActiva())
                .estado(ventanilla.getEstadoVentanilla())
                .servicios(servicios)
                .build();
    }
}
