package com.multiservicio.repository;

import com.multiservicio.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    List<Servicio> findByActivoTrue();

    Optional<Servicio> findByPrefijo(String prefijo);

    boolean existsByNombre(String nombre);

    boolean existsByPrefijo(String prefijo);
}
