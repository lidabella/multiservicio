package com.multiservicio.repository;

import com.multiservicio.entity.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    Optional<Turno> findByCodigo(String codigo);

    long countByServicioIdAndFecha(Long servicioId, LocalDate fecha);

    List<Turno> findByServicioIdAndFechaAndEstadoOrderByFechaCreacionAsc(
            Long servicioId, LocalDate fecha, String estado);

    @Query("""
            SELECT t FROM Turno t
            JOIN FETCH t.servicio s
            JOIN FETCH t.prioridad p
            WHERE t.estado = :estado
              AND t.fecha = :fecha
              AND (:servicioId IS NULL OR s.id = :servicioId)
            ORDER BY p.peso DESC, t.fechaCreacion ASC
            """)
    List<Turno> findPendientes(
            @Param("servicioId") Long servicioId,
            @Param("fecha") LocalDate fecha,
            @Param("estado") String estado);

    List<Turno> findByEstadoAndFechaOrderByFechaLlamadoDesc(
            String estado, LocalDate fecha);

    List<Turno> findTop4ByServicioIdAndFechaAndFechaLlamadoNotNullOrderByFechaLlamadoDesc(
            Long servicioId, LocalDate fecha);

    @Query("""
            SELECT t FROM Turno t
            JOIN FETCH t.servicio s
            JOIN FETCH t.prioridad p
            WHERE t.fecha = :fecha
              AND (:servicioId IS NULL OR s.id = :servicioId)
            """)
    List<Turno> findByFechaAndServicio(
            @Param("fecha") LocalDate fecha,
            @Param("servicioId") Long servicioId);

    @Query("""
            SELECT t FROM Turno t
            JOIN FETCH t.servicio s
            JOIN FETCH t.prioridad p
            LEFT JOIN FETCH t.ventanilla v
            WHERE t.fecha = :fecha
              AND t.estado IN :estados
              AND (:servicioId IS NULL OR s.id = :servicioId)
            ORDER BY t.fechaLlamado DESC
            """)
    List<Turno> findRecientesPorEstados(
            @Param("fecha") LocalDate fecha,
            @Param("servicioId") Long servicioId,
            @Param("estados") List<String> estados);
}
