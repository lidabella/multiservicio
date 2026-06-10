package com.multiservicio.service;

import com.multiservicio.dto.DashboardResponse;
import com.multiservicio.entity.Turno;
import com.multiservicio.entity.enums.EstadoTurno;
import com.multiservicio.repository.TurnoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TurnoRepository turnoRepository;
    private final ServicioService servicioService;

    @Transactional(readOnly = true)
    public DashboardResponse obtener(LocalDate fecha, Long servicioId) {
        List<Turno> turnos = turnoRepository.findByFechaAndServicio(fecha, servicioId);

        long pendientes = contar(turnos, EstadoTurno.PENDIENTE);
        long llamados = contar(turnos, EstadoTurno.LLAMADO);
        long enAtencion = contar(turnos, EstadoTurno.EN_ATENCION);
        long atendidos = contar(turnos, EstadoTurno.FINALIZADO);
        long cancelados = contar(turnos, EstadoTurno.CANCELADO);
        long noPresentados = contar(turnos, EstadoTurno.NO_PRESENTADO);
        long reprogramados = contar(turnos, EstadoTurno.REPROGRAMADO);

        double promedioEspera = turnos.stream()
                .filter(t -> t.getFechaLlamado() != null && t.getFechaCreacion() != null)
                .mapToLong(t -> Duration.between(t.getFechaCreacion(), t.getFechaLlamado()).toMinutes())
                .average()
                .orElse(0.0);

        double promedioAtencion = turnos.stream()
                .filter(t -> t.getFechaInicioAtencion() != null && t.getFechaFinalizacion() != null)
                .mapToLong(t -> Duration.between(t.getFechaInicioAtencion(), t.getFechaFinalizacion()).toMinutes())
                .average()
                .orElse(0.0);

        String servicioNombre = null;
        if (servicioId != null) {
            servicioNombre = servicioService.obtener(servicioId).getNombre();
        }

        return DashboardResponse.builder()
                .fecha(fecha.toString())
                .servicioId(servicioId)
                .servicioNombre(servicioNombre)
                .pendientes(pendientes)
                .llamados(llamados)
                .enAtencion(enAtencion)
                .atendidos(atendidos)
                .cancelados(cancelados)
                .noPresentados(noPresentados)
                .reprogramados(reprogramados)
                .totalTurnos(turnos.size())
                .promedioEsperaMinutos(redondear(promedioEspera))
                .promedioAtencionMinutos(redondear(promedioAtencion))
                .build();
    }

    private long contar(List<Turno> turnos, EstadoTurno estado) {
        return turnos.stream().filter(t -> t.getEstadoTurno() == estado).count();
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
