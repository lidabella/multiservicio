package com.multiservicio.controller;

import com.multiservicio.dto.*;
import com.multiservicio.service.TurnoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/turnos")
@RequiredArgsConstructor
public class TurnoController {

    private final TurnoService turnoService;

    @PostMapping
    public ResponseEntity<ApiResponse<TurnoResponse>> crear(@Valid @RequestBody TurnoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<TurnoResponse>builder()
                        .success(true)
                        .message("Turno creado")
                        .data(turnoService.crear(request))
                        .build());
    }

    @GetMapping("/llamados")
    public ResponseEntity<ApiResponse<List<TurnoResponse>>> llamados(
            @RequestParam(required = false) Long servicioId) {
        return ResponseEntity.ok(ApiResponse.<List<TurnoResponse>>builder()
                .success(true)
                .data(turnoService.listarLlamadosRecientes(servicioId))
                .build());
    }

    @GetMapping("/pendientes")
    public ResponseEntity<ApiResponse<List<TurnoResponse>>> pendientes(
            @RequestParam(required = false) Long servicioId) {
        return ResponseEntity.ok(ApiResponse.<List<TurnoResponse>>builder()
                .success(true)
                .data(turnoService.listarPendientes(servicioId))
                .build());
    }

    @PostMapping("/llamar")
    public ResponseEntity<ApiResponse<TurnoResponse>> llamar(@Valid @RequestBody LlamarTurnoRequest request) {
        return ResponseEntity.ok(ApiResponse.<TurnoResponse>builder()
                .success(true)
                .message("Turno llamado")
                .data(turnoService.llamarSiguiente(request))
                .build());
    }

    @PatchMapping("/{id}/iniciar")
    public ResponseEntity<ApiResponse<TurnoResponse>> iniciar(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<TurnoResponse>builder()
                .success(true)
                .message("Atención iniciada")
                .data(turnoService.iniciarAtencion(id))
                .build());
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<ApiResponse<TurnoResponse>> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<TurnoResponse>builder()
                .success(true)
                .message("Atención finalizada")
                .data(turnoService.finalizarAtencion(id))
                .build());
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ApiResponse<TurnoResponse>> cancelar(
            @PathVariable Long id, @Valid @RequestBody CancelarTurnoRequest request) {
        return ResponseEntity.ok(ApiResponse.<TurnoResponse>builder()
                .success(true)
                .message("Turno cancelado")
                .data(turnoService.cancelar(id, request))
                .build());
    }

    @PatchMapping("/{id}/no-presentado")
    public ResponseEntity<ApiResponse<TurnoResponse>> noPresentado(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<TurnoResponse>builder()
                .success(true)
                .message("Turno marcado como no presentado")
                .data(turnoService.marcarNoPresentado(id))
                .build());
    }

    @PatchMapping("/{id}/reprogramar")
    public ResponseEntity<ApiResponse<TurnoResponse>> reprogramar(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<TurnoResponse>builder()
                .success(true)
                .message("Turno reprogramado")
                .data(turnoService.reprogramar(id))
                .build());
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<ApiResponse<List<HistorialResponse>>> historial(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<List<HistorialResponse>>builder()
                .success(true)
                .data(turnoService.obtenerHistorial(id))
                .build());
    }
}
