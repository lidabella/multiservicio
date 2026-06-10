package com.multiservicio.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TurnoRequest {

    @NotNull(message = "El servicio es obligatorio")
    private Long servicioId;

    @NotNull(message = "La prioridad es obligatoria")
    private Long prioridadId;
}
