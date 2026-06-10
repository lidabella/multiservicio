package com.multiservicio.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrioridadResponse {

    private Long id;
    private String nombre;
    private Integer peso;
    private Integer tiempoMaximoEspera;
}
