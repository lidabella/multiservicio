package com.multiservicio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PrioridadRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50)
    private String nombre;

    @NotNull(message = "El peso es obligatorio")
    @Positive(message = "El peso debe ser mayor a 0")
    private Integer peso;

    @NotNull(message = "El tiempo máximo de espera es obligatorio")
    @Positive(message = "El tiempo máximo debe ser mayor a 0")
    private Integer tiempoMaximoEspera;
}
