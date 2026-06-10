package com.multiservicio.repository;

import com.multiservicio.entity.Ventanilla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentanillaRepository extends JpaRepository<Ventanilla, Long> {

    List<Ventanilla> findByActivaTrue();

    List<Ventanilla> findByEstadoAndActivaTrue(String estado);

    @Query("""
            SELECT v FROM Ventanilla v
            JOIN v.servicios s
            WHERE s.id = :servicioId
              AND v.activa = true
              AND v.estado = :estado
            """)
    List<Ventanilla> findDisponiblesPorServicio(
            @Param("servicioId") Long servicioId,
            @Param("estado") String estado);

    @Query("""
            SELECT v FROM Ventanilla v
            JOIN v.servicios s
            WHERE s.id = :servicioId
            """)
    List<Ventanilla> findByServicioId(@Param("servicioId") Long servicioId);
}
