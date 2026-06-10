package com.multiservicio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ServicioRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @Size(max = 255)
    private String descripcion;

    @NotBlank(message = "El prefijo es obligatorio")
    @Size(max = 10)
    private String prefijo;

    @NotNull(message = "La duración estimada es obligatoria")
    @Positive(message = "La duración debe ser mayor a 0")
    private Integer duracionEstimada;

    private Boolean activo = true;
}
