package com.multiservicio.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class HistorialResponse {

    private Long id;
    private String usuario;
    private String ventanilla;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String estadoAnterior;
    private String estadoNuevo;
    private String observaciones;
}
