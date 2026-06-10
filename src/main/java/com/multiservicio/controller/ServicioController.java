package com.multiservicio.controller;

import com.multiservicio.dto.ApiResponse;
import com.multiservicio.dto.ServicioRequest;
import com.multiservicio.dto.ServicioResponse;
import com.multiservicio.service.ServicioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
@RequiredArgsConstructor
public class ServicioController {

    private final ServicioService servicioService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ServicioResponse>>> listar(
            @RequestParam(required = false) Boolean activos) {
        return ResponseEntity.ok(ApiResponse.<List<ServicioResponse>>builder()
                .success(true)
                .message("Servicios obtenidos")
                .data(servicioService.listar(activos))
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServicioResponse>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<ServicioResponse>builder()
                .success(true)
                .data(servicioService.obtener(id))
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ServicioResponse>> crear(@Valid @RequestBody ServicioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ServicioResponse>builder()
                        .success(true)
                        .message("Servicio creado")
                        .data(servicioService.crear(request))
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ServicioResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody ServicioRequest request) {
        return ResponseEntity.ok(ApiResponse.<ServicioResponse>builder()
                .success(true)
                .message("Servicio actualizado")
                .data(servicioService.actualizar(id, request))
                .build());
    }
}
