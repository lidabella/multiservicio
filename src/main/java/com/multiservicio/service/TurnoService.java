package com.multiservicio.service;

import com.multiservicio.dto.*;
import com.multiservicio.entity.*;
import com.multiservicio.entity.enums.EstadoTurno;
import com.multiservicio.entity.enums.EstadoVentanilla;
import com.multiservicio.exception.AuthException;
import com.multiservicio.repository.AuditoriaRepository;
import com.multiservicio.repository.HistorialTurnoRepository;
import com.multiservicio.repository.TurnoRepository;
import com.multiservicio.repository.VentanillaRepository;
import com.multiservicio.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TurnoService {

    private static final int AGING_BONUS = 1000;
    private static final int FAIRNESS_WINDOW = 4;
    private static final int MAX_PRIORITY_IN_WINDOW = 3;

    private final TurnoRepository turnoRepository;
    private final HistorialTurnoRepository historialTurnoRepository;
    private final AuditoriaRepository auditoriaRepository;
    private final VentanillaRepository ventanillaRepository;
    private final ServicioService servicioService;
    private final PrioridadService prioridadService;
    private final VentanillaService ventanillaService;
    private final SecurityUtils securityUtils;

    @Transactional
    public TurnoResponse crear(TurnoRequest request) {
        Servicio servicio = servicioService.buscarActivo(request.getServicioId());
        Prioridad prioridad = prioridadService.buscar(request.getPrioridadId());
        LocalDate hoy = LocalDate.now();
        LocalDateTime ahora = LocalDateTime.now();

        Turno turno = new Turno();
        turno.setCodigo(generarCodigo(servicio, hoy));
        turno.setFecha(hoy);
        turno.setEstadoTurno(EstadoTurno.PENDIENTE);
        turno.setServicio(servicio);
        turno.setPrioridad(prioridad);
        turno.setFechaCreacion(ahora);
        turno.setFechaActualizacion(ahora);

        Turno guardado = turnoRepository.save(turno);
        Usuario usuario = securityUtils.getUsuarioActual();
        registrarHistorial(guardado, null, EstadoTurno.PENDIENTE, usuario,
                resolverVentanillaParaHistorial(guardado), "Turno creado");
        registrarAuditoria(usuario, "Creó turno " + guardado.getCodigo());

        return toResponse(guardado);
    }

    @Transactional(readOnly = true)
    public List<TurnoResponse> listarLlamadosRecientes(Long servicioId) {
        LocalDate hoy = LocalDate.now();
        return turnoRepository.findRecientesPorEstados(
                        hoy, servicioId,
                        List.of(
                                EstadoTurno.LLAMADO.getDbValue(),
                                EstadoTurno.EN_ATENCION.getDbValue(),
                                EstadoTurno.FINALIZADO.getDbValue()))
                .stream()
                .limit(10)
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TurnoResponse> listarPendientes(Long servicioId) {
        LocalDate hoy = LocalDate.now();
        return turnoRepository.findPendientes(servicioId, hoy, EstadoTurno.PENDIENTE.getDbValue())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public TurnoResponse llamarSiguiente(LlamarTurnoRequest request) {
        Ventanilla ventanilla = ventanillaService.buscarDisponibleParaServicio(
                request.getVentanillaId(), request.getServicioId());

        LocalDate hoy = LocalDate.now();
        List<Turno> pendientes = turnoRepository.findPendientes(
                request.getServicioId(), hoy, EstadoTurno.PENDIENTE.getDbValue());

        if (pendientes.isEmpty()) {
            throw new AuthException("No hay turnos pendientes para este servicio", HttpStatus.NOT_FOUND);
        }

        int pesoRegular = prioridadService.obtenerPesoRegular();
        Turno seleccionado = seleccionarTurno(pendientes, request.getServicioId(), hoy, pesoRegular);

        LocalDateTime ahora = LocalDateTime.now();
        EstadoTurno estadoAnterior = seleccionado.getEstadoTurno();
        seleccionado.setEstadoTurno(EstadoTurno.LLAMADO);
        seleccionado.setFechaLlamado(ahora);
        seleccionado.setFechaActualizacion(ahora);
        seleccionado.setVentanilla(ventanilla);

        ventanilla.setEstadoVentanilla(EstadoVentanilla.OCUPADA);
        ventanillaRepository.save(ventanilla);

        Usuario usuario = securityUtils.getUsuarioActual();
        registrarHistorial(seleccionado, estadoAnterior, EstadoTurno.LLAMADO, usuario, ventanilla,
                "Turno llamado aplicando prioridad, FIFO, aging y fairness");
        registrarAuditoria(usuario, "Llamó turno " + seleccionado.getCodigo());

        return toResponse(turnoRepository.save(seleccionado));
    }

    @Transactional
    public TurnoResponse iniciarAtencion(Long turnoId) {
        Turno turno = buscar(turnoId);
        validarEstado(turno, EstadoTurno.LLAMADO);

        LocalDateTime ahora = LocalDateTime.now();
        EstadoTurno anterior = turno.getEstadoTurno();
        turno.setEstadoTurno(EstadoTurno.EN_ATENCION);
        turno.setFechaInicioAtencion(ahora);
        turno.setFechaActualizacion(ahora);

        Usuario usuario = securityUtils.getUsuarioActual();
        registrarHistorial(turno, anterior, EstadoTurno.EN_ATENCION, usuario, turno.getVentanilla(),
                "Atención iniciada");

        return toResponse(turnoRepository.save(turno));
    }

    @Transactional
    public TurnoResponse finalizarAtencion(Long turnoId) {
        Turno turno = buscar(turnoId);
        validarEstado(turno, EstadoTurno.EN_ATENCION);

        LocalDateTime ahora = LocalDateTime.now();
        EstadoTurno anterior = turno.getEstadoTurno();
        turno.setEstadoTurno(EstadoTurno.FINALIZADO);
        turno.setFechaFinalizacion(ahora);
        turno.setFechaActualizacion(ahora);

        liberarVentanilla(turno.getVentanilla());

        Usuario usuario = securityUtils.getUsuarioActual();
        registrarHistorial(turno, anterior, EstadoTurno.FINALIZADO, usuario, turno.getVentanilla(),
                "Atención finalizada");

        return toResponse(turnoRepository.save(turno));
    }

    @Transactional
    public TurnoResponse cancelar(Long turnoId, CancelarTurnoRequest request) {
        Turno turno = buscar(turnoId);
        if (turno.getEstadoTurno() == EstadoTurno.FINALIZADO
                || turno.getEstadoTurno() == EstadoTurno.CANCELADO
                || turno.getEstadoTurno() == EstadoTurno.NO_PRESENTADO) {
            throw new AuthException("No se puede cancelar un turno en estado " + turno.getEstadoTurno().getDbValue(),
                    HttpStatus.BAD_REQUEST);
        }

        EstadoTurno anterior = turno.getEstadoTurno();
        turno.setEstadoTurno(EstadoTurno.CANCELADO);
        turno.setFechaActualizacion(LocalDateTime.now());

        if (turno.getVentanilla() != null) {
            liberarVentanilla(turno.getVentanilla());
        }

        Usuario usuario = securityUtils.getUsuarioActual();
        registrarHistorial(turno, anterior, EstadoTurno.CANCELADO, usuario, turno.getVentanilla(),
                request.getMotivo());

        return toResponse(turnoRepository.save(turno));
    }

    @Transactional
    public TurnoResponse marcarNoPresentado(Long turnoId) {
        Turno turno = buscar(turnoId);
        validarEstado(turno, EstadoTurno.LLAMADO);

        EstadoTurno anterior = turno.getEstadoTurno();
        turno.setEstadoTurno(EstadoTurno.NO_PRESENTADO);
        turno.setFechaActualizacion(LocalDateTime.now());

        liberarVentanilla(turno.getVentanilla());

        Usuario usuario = securityUtils.getUsuarioActual();
        registrarHistorial(turno, anterior, EstadoTurno.NO_PRESENTADO, usuario, turno.getVentanilla(),
                "Usuario no se presentó");

        return toResponse(turnoRepository.save(turno));
    }

    @Transactional
    public TurnoResponse reprogramar(Long turnoId) {
        Turno turno = buscar(turnoId);
        if (turno.getEstadoTurno() != EstadoTurno.PENDIENTE && turno.getEstadoTurno() != EstadoTurno.LLAMADO) {
            throw new AuthException("Solo se pueden reprogramar turnos pendientes o llamados", HttpStatus.BAD_REQUEST);
        }

        EstadoTurno anterior = turno.getEstadoTurno();
        turno.setEstadoTurno(EstadoTurno.REPROGRAMADO);
        turno.setFechaActualizacion(LocalDateTime.now());

        if (turno.getVentanilla() != null) {
            liberarVentanilla(turno.getVentanilla());
            turno.setVentanilla(null);
        }

        Usuario usuario = securityUtils.getUsuarioActual();
        registrarHistorial(turno, anterior, EstadoTurno.REPROGRAMADO, usuario, null, "Turno reprogramado");

        return toResponse(turnoRepository.save(turno));
    }

    @Transactional(readOnly = true)
    public List<HistorialResponse> obtenerHistorial(Long turnoId) {
        buscar(turnoId);
        return historialTurnoRepository.findByTurnoIdOrderByFechaAscHoraInicioAsc(turnoId)
                .stream()
                .map(h -> HistorialResponse.builder()
                        .id(h.getId())
                        .usuario(h.getUsuario().getNombre())
                        .ventanilla(h.getVentanilla() != null ? h.getVentanilla().getNombre() : null)
                        .fecha(h.getFecha())
                        .horaInicio(h.getHoraInicio())
                        .horaFin(h.getHoraFin())
                        .estadoAnterior(h.getEstadoAnterior())
                        .estadoNuevo(h.getEstadoNuevo())
                        .observaciones(h.getObservaciones())
                        .build())
                .toList();
    }

    Turno seleccionarTurno(List<Turno> pendientes, Long servicioId, LocalDate fecha, int pesoRegular) {
        boolean hayRegulares = pendientes.stream()
                .anyMatch(t -> t.getPrioridad().getPeso().equals(pesoRegular));

        List<Turno> candidatos = pendientes;
        if (hayRegulares && debeForzarRegular(servicioId, fecha, pesoRegular)) {
            candidatos = pendientes.stream()
                    .filter(t -> t.getPrioridad().getPeso().equals(pesoRegular))
                    .toList();
        }

        return candidatos.stream()
                .map(t -> new TurnoOrdenado(t, calcularPesoEfectivo(t, pesoRegular)))
                .sorted(Comparator
                        .comparingInt(TurnoOrdenado::pesoEfectivo).reversed()
                        .thenComparing(t -> t.turno().getFechaCreacion()))
                .map(TurnoOrdenado::turno)
                .findFirst()
                .orElseThrow(() -> new AuthException("No se pudo seleccionar turno", HttpStatus.CONFLICT));
    }

    private int calcularPesoEfectivo(Turno turno, int pesoRegular) {
        int peso = turno.getPrioridad().getPeso();
        if (peso != pesoRegular) {
            return peso;
        }

        long minutosEspera = Duration.between(turno.getFechaCreacion(), LocalDateTime.now()).toMinutes();
        if (minutosEspera >= turno.getPrioridad().getTiempoMaximoEspera()) {
            return peso + AGING_BONUS;
        }
        return peso;
    }

    private boolean debeForzarRegular(Long servicioId, LocalDate fecha, int pesoRegular) {
        List<Turno> ultimosLlamados = turnoRepository
                .findTop4ByServicioIdAndFechaAndFechaLlamadoNotNullOrderByFechaLlamadoDesc(servicioId, fecha);

        if (ultimosLlamados.size() < FAIRNESS_WINDOW) {
            return false;
        }

        long prioritarios = ultimosLlamados.stream()
                .filter(t -> t.getPrioridad().getPeso() > pesoRegular)
                .count();

        return prioritarios >= MAX_PRIORITY_IN_WINDOW;
    }

    private String generarCodigo(Servicio servicio, LocalDate fecha) {
        long cantidad = turnoRepository.countByServicioIdAndFecha(servicio.getId(), fecha);
        String fechaStr = fecha.format(DateTimeFormatter.BASIC_ISO_DATE);
        return String.format("%s-%s-%03d", servicio.getPrefijo(), fechaStr, cantidad + 1);
    }

    private void liberarVentanilla(Ventanilla ventanilla) {
        if (ventanilla != null) {
            ventanilla.setEstadoVentanilla(EstadoVentanilla.DISPONIBLE);
            ventanillaRepository.save(ventanilla);
        }
    }

    private void validarEstado(Turno turno, EstadoTurno esperado) {
        if (turno.getEstadoTurno() != esperado) {
            throw new AuthException(
                    "Transición inválida: el turno está en " + turno.getEstadoTurno().getDbValue(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    private Turno buscar(Long id) {
        return turnoRepository.findById(id)
                .orElseThrow(() -> new AuthException("Turno no encontrado", HttpStatus.NOT_FOUND));
    }

    private void registrarHistorial(Turno turno, EstadoTurno anterior, EstadoTurno nuevo,
                                    Usuario usuario, Ventanilla ventanilla, String observacion) {
        HistorialTurno historial = new HistorialTurno();
        historial.setTurno(turno);
        historial.setUsuario(usuario);
        historial.setVentanilla(ventanilla != null ? ventanilla : resolverVentanillaParaHistorial(turno));
        historial.setFecha(LocalDate.now());
        historial.setHoraInicio(LocalTime.now());
        historial.setEstadoAnterior(anterior != null ? anterior.getDbValue() : null);
        historial.setEstadoNuevo(nuevo.getDbValue());
        historial.setObservaciones(observacion);
        historialTurnoRepository.save(historial);
    }

    private Ventanilla resolverVentanillaParaHistorial(Turno turno) {
        if (turno.getVentanilla() != null) {
            return turno.getVentanilla();
        }
        return ventanillaRepository.findByServicioId(turno.getServicio().getId()).stream()
                .findFirst()
                .orElseThrow(() -> new AuthException(
                        "No hay ventanilla asociada al servicio para registrar historial",
                        HttpStatus.BAD_REQUEST));
    }

    private void registrarAuditoria(Usuario usuario, String accion) {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario(usuario);
        auditoria.setAccion(accion);
        auditoria.setFecha(LocalDate.now());
        auditoria.setHora(LocalTime.now());
        auditoriaRepository.save(auditoria);
    }

    private TurnoResponse toResponse(Turno turno) {
        return TurnoResponse.builder()
                .id(turno.getId())
                .codigo(turno.getCodigo())
                .fecha(turno.getFecha())
                .estado(turno.getEstadoTurno())
                .servicioId(turno.getServicio().getId())
                .servicioNombre(turno.getServicio().getNombre())
                .servicioPrefijo(turno.getServicio().getPrefijo())
                .prioridadId(turno.getPrioridad().getId())
                .prioridadNombre(turno.getPrioridad().getNombre())
                .prioridadPeso(turno.getPrioridad().getPeso())
                .ventanillaId(turno.getVentanilla() != null ? turno.getVentanilla().getId() : null)
                .ventanillaNombre(turno.getVentanilla() != null ? turno.getVentanilla().getNombre() : null)
                .fechaCreacion(turno.getFechaCreacion())
                .fechaLlamado(turno.getFechaLlamado())
                .fechaInicioAtencion(turno.getFechaInicioAtencion())
                .fechaFinalizacion(turno.getFechaFinalizacion())
                .build();
    }

    private record TurnoOrdenado(Turno turno, int pesoEfectivo) {}
}
