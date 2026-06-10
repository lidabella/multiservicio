package com.multiservicio.controller;

import com.multiservicio.dto.ApiResponse;
import com.multiservicio.dto.PrioridadRequest;
import com.multiservicio.dto.PrioridadResponse;
import com.multiservicio.service.PrioridadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prioridades")
@RequiredArgsConstructor
public class PrioridadController {

    private final PrioridadService prioridadService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PrioridadResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.<List<PrioridadResponse>>builder()
                .success(true)
                .data(prioridadService.listar())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PrioridadResponse>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<PrioridadResponse>builder()
                .success(true)
                .data(prioridadService.obtener(id))
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PrioridadResponse>> crear(@Valid @RequestBody PrioridadRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<PrioridadResponse>builder()
                        .success(true)
                        .message("Prioridad creada")
                        .data(prioridadService.crear(request))
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PrioridadResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody PrioridadRequest request) {
        return ResponseEntity.ok(ApiResponse.<PrioridadResponse>builder()
                .success(true)
                .message("Prioridad actualizada")
                .data(prioridadService.actualizar(id, request))
                .build());
    }
}
