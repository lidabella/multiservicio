package com.multiservicio.repository;

import com.multiservicio.entity.HistorialTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialTurnoRepository extends JpaRepository<HistorialTurno, Long> {

    List<HistorialTurno> findByTurnoIdOrderByFechaAscHoraInicioAsc(Long turnoId);
}
