package com.multiservicio.dto;

import com.multiservicio.entity.enums.EstadoVentanilla;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class VentanillaRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @Size(max = 150)
    private String ubicacion;

    private Boolean activa = true;

    private EstadoVentanilla estado = EstadoVentanilla.DISPONIBLE;

    private List<Long> servicioIds;
}
