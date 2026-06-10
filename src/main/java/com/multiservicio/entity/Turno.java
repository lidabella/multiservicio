package com.multiservicio.entity;

import com.multiservicio.entity.enums.EstadoTurno;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "turnos")
@Access(AccessType.FIELD)
@Getter
@Setter
@NoArgsConstructor
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "estado", nullable = false, columnDefinition =
            "ENUM('PENDIENTE','LLAMADO','EN ATENCION','FINALIZADO','CANCELADO','NO PRESENTADO','REPROGRAMADO')")
    private String estado = EstadoTurno.PENDIENTE.getDbValue();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prioridad_id", nullable = false)
    private Prioridad prioridad;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_llamado")
    private LocalDateTime fechaLlamado;

    @Column(name = "fecha_inicio_atencion")
    private LocalDateTime fechaInicioAtencion;

    @Column(name = "fecha_finalizacion")
    private LocalDateTime fechaFinalizacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ventanilla_id")
    private Ventanilla ventanilla;

    public EstadoTurno getEstadoTurno() {
        return EstadoTurno.fromDb(estado);
    }

    public void setEstadoTurno(EstadoTurno estadoTurno) {
        this.estado = estadoTurno != null ? estadoTurno.getDbValue() : null;
    }
}
