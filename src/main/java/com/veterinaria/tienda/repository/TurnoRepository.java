package com.veterinaria.tienda.repository;

import com.veterinaria.tienda.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    // Buscar turnos por veterinario y fecha/hora
    List<Turno> findByVeterinarioAndFechaHoraAndEstado(
        String veterinario, 
        LocalDateTime fechaHora, 
        String estado
    );
    
    // Buscar turnos activos por fecha/hora
    List<Turno> findByFechaHoraAndEstado(LocalDateTime fechaHora, String estado);
    
    // Contar turnos de un dueño en un día específico
    @Query("SELECT COUNT(t) FROM Turno t WHERE t.nombreDueno = :dueno " +
           "AND DATE(t.fechaHora) = DATE(:fecha) AND t.estado = 'ACTIVO'")
    long countTurnosByDuenoAndFecha(
        @Param("dueno") String dueno, 
        @Param("fecha") LocalDateTime fecha
    );
    
    // Buscar turnos por especie y fecha/hora
    @Query("SELECT t FROM Turno t WHERE t.especie IN :especies " +
           "AND t.fechaHora = :fechaHora AND t.estado = 'ACTIVO'")
    List<Turno> findByEspeciesAndFechaHora(
        @Param("especies") List<String> especies, 
        @Param("fechaHora") LocalDateTime fechaHora
    );
    
    // Buscar todos los turnos ordenados por fecha
    List<Turno> findAllByOrderByFechaHoraDesc();
    
    // Buscar turnos cancelados en un rango de fechas
    @Query("SELECT t FROM Turno t WHERE t.estado = 'CANCELADO' " +
           "AND t.fechaCancelacion BETWEEN :inicio AND :fin " +
           "ORDER BY t.fechaCancelacion DESC")
    List<Turno> findTurnosCanceladosByFechaRange(
        @Param("inicio") LocalDateTime inicio, 
        @Param("fin") LocalDateTime fin
    );
    
    // Buscar turnos activos por veterinario y fecha
    @Query("SELECT t FROM Turno t WHERE t.veterinario = :vet " +
           "AND DATE(t.fechaHora) = DATE(:fecha) AND t.estado = 'ACTIVO' " +
           "ORDER BY t.fechaHora ASC")
    List<Turno> findTurnosActivosByVeterinarioAndFecha(
        @Param("vet") String veterinario, 
        @Param("fecha") LocalDateTime fecha
    );
    
}
