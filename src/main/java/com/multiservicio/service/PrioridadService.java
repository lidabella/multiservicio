package com.multiservicio.service;

import com.multiservicio.dto.PrioridadRequest;
import com.multiservicio.dto.PrioridadResponse;
import com.multiservicio.entity.Prioridad;
import com.multiservicio.exception.AuthException;
import com.multiservicio.repository.PrioridadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrioridadService {

    private final PrioridadRepository prioridadRepository;

    @Transactional(readOnly = true)
    public List<PrioridadResponse> listar() {
        return prioridadRepository.findAll().stream()
                .sorted(Comparator.comparing(Prioridad::getPeso).reversed())
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PrioridadResponse obtener(Long id) {
        return toResponse(buscar(id));
    }

    @Transactional
    public PrioridadResponse crear(PrioridadRequest request) {
        prioridadRepository.findByNombre(request.getNombre()).ifPresent(p -> {
            throw new AuthException("Ya existe esa prioridad", HttpStatus.CONFLICT);
        });

        Prioridad prioridad = new Prioridad();
        aplicarDatos(prioridad, request);
        return toResponse(prioridadRepository.save(prioridad));
    }

    @Transactional
    public PrioridadResponse actualizar(Long id, PrioridadRequest request) {
        Prioridad prioridad = buscar(id);
        aplicarDatos(prioridad, request);
        return toResponse(prioridadRepository.save(prioridad));
    }

    public Prioridad buscar(Long id) {
        return prioridadRepository.findById(id)
                .orElseThrow(() -> new AuthException("Prioridad no encontrada", HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public int obtenerPesoRegular() {
        return prioridadRepository.findAll().stream()
                .mapToInt(Prioridad::getPeso)
                .min()
                .orElse(1);
    }

    private void aplicarDatos(Prioridad prioridad, PrioridadRequest request) {
        prioridad.setNombre(request.getNombre());
        prioridad.setPeso(request.getPeso());
        prioridad.setTiempoMaximoEspera(request.getTiempoMaximoEspera());
    }

    private PrioridadResponse toResponse(Prioridad prioridad) {
        return PrioridadResponse.builder()
                .id(prioridad.getId())
                .nombre(prioridad.getNombre())
                .peso(prioridad.getPeso())
                .tiempoMaximoEspera(prioridad.getTiempoMaximoEspera())
                .build();
    }
}
