package com.multiservicio.dto;

import com.multiservicio.entity.enums.EstadoVentanilla;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VentanillaResponse {

    private Long id;
    private String nombre;
    private String ubicacion;
    private Boolean activa;
    private EstadoVentanilla estado;
    private List<ServicioResponse> servicios;
}
