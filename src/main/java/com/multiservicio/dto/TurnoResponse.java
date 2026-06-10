package com.multiservicio.dto;

import com.multiservicio.entity.enums.EstadoTurno;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class TurnoResponse {

    private Long id;
    private String codigo;
    private LocalDate fecha;
    private EstadoTurno estado;
    private Long servicioId;
    private String servicioNombre;
    private String servicioPrefijo;
    private Long prioridadId;
    private String prioridadNombre;
    private Integer prioridadPeso;
    private Long ventanillaId;
    private String ventanillaNombre;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaLlamado;
    private LocalDateTime fechaInicioAtencion;
    private LocalDateTime fechaFinalizacion;
}
