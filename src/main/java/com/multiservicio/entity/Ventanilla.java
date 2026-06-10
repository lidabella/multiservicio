package com.multiservicio.entity;

import com.multiservicio.entity.enums.EstadoVentanilla;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ventanillas")
@Access(AccessType.FIELD)
@Getter
@Setter
@NoArgsConstructor
public class Ventanilla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 150)
    private String ubicacion;

    @Column(nullable = false)
    private Boolean activa = true;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "estado", nullable = false, columnDefinition =
            "ENUM('DISPONIBLE','OCUPADA','PAUSADA')")
    private String estado = EstadoVentanilla.DISPONIBLE.getDbValue();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "ventanilla_servicio",
            joinColumns = @JoinColumn(name = "ventanilla_id"),
            inverseJoinColumns = @JoinColumn(name = "servicio_id")
    )
    private Set<Servicio> servicios = new HashSet<>();

    public EstadoVentanilla getEstadoVentanilla() {
        return EstadoVentanilla.fromDb(estado);
    }

    public void setEstadoVentanilla(EstadoVentanilla estadoVentanilla) {
        this.estado = estadoVentanilla != null ? estadoVentanilla.getDbValue() : null;
    }
}
