package com.multiservicio.controller;

import com.multiservicio.dto.ApiResponse;
import com.multiservicio.dto.VentanillaRequest;
import com.multiservicio.dto.VentanillaResponse;
import com.multiservicio.service.VentanillaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventanillas")
@RequiredArgsConstructor
public class VentanillaController {

    private final VentanillaService ventanillaService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<VentanillaResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.<List<VentanillaResponse>>builder()
                .success(true)
                .data(ventanillaService.listar())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VentanillaResponse>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<VentanillaResponse>builder()
                .success(true)
                .data(ventanillaService.obtener(id))
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VentanillaResponse>> crear(@Valid @RequestBody VentanillaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<VentanillaResponse>builder()
                        .success(true)
                        .message("Ventanilla creada")
                        .data(ventanillaService.crear(request))
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VentanillaResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody VentanillaRequest request) {
        return ResponseEntity.ok(ApiResponse.<VentanillaResponse>builder()
                .success(true)
                .message("Ventanilla actualizada")
                .data(ventanillaService.actualizar(id, request))
                .build());
    }
}
