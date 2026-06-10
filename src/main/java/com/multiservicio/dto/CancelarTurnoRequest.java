package com.multiservicio.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CancelarTurnoRequest {

    @NotBlank(message = "El motivo es obligatorio")
    private String motivo;
}
