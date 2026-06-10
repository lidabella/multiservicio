package com.multiservicio.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LlamarTurnoRequest {

    @NotNull(message = "El servicio es obligatorio")
    private Long servicioId;

    @NotNull(message = "La ventanilla es obligatoria")
    private Long ventanillaId;
}
