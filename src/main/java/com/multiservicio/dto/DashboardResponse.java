package com.multiservicio.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponse {

    private String fecha;
    private Long servicioId;
    private String servicioNombre;
    private long pendientes;
    private long llamados;
    private long enAtencion;
    private long atendidos;
    private long cancelados;
    private long noPresentados;
    private long reprogramados;
    private long totalTurnos;
    private double promedioEsperaMinutos;
    private double promedioAtencionMinutos;
}
