package com.veterinaria.tienda.service;


import com.veterinaria.tienda.model.Turno;
import com.veterinaria.tienda.repository.TurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class TurnoService {


    @Autowired
    private TurnoRepository turnoRepository;
    
    // Validar y guardar turno
    public Turno guardarTurno(Turno turno) throws Exception {
        // Validación 1: Cruce de horarios con mismo veterinario
        List<Turno> cruceVeterinario = turnoRepository
            .findByVeterinarioAndFechaHoraAndEstado(
                turno.getVeterinario(), 
                turno.getFechaHora(), 
                "ACTIVO"
            );
        
        if (!cruceVeterinario.isEmpty()) {
            LocalDateTime sugerida = sugerirHoraDisponible(
                turno.getVeterinario(), 
                turno.getFechaHora()
            );
            throw new Exception("El horario ya está ocupado. " +
                "Hora sugerida: " + sugerida);
        }
        
        // Validación 2: Compatibilidad de especies (perros y gatos)
        if (turno.getEspecie().equalsIgnoreCase("Perro") || 
            turno.getEspecie().equalsIgnoreCase("Gato")) {
            
            List<String> especiesIncompatibles = Arrays.asList("Perro", "Gato");
            List<Turno> cruceEspecie = turnoRepository
                .findByEspeciesAndFechaHora(
                    especiesIncompatibles, 
                    turno.getFechaHora()
                );
            
            if (!cruceEspecie.isEmpty()) {
                throw new Exception("No se pueden agendar perros y gatos " +
                    "en la misma hora");
            }
        }
        
        // Validación 3: Límite de turnos por dueño (2 por día)
        long turnosDueno = turnoRepository.countTurnosByDuenoAndFecha(
            turno.getNombreDueno(), 
            turno.getFechaHora()
        );
        
        if (turnosDueno >= 2) {
            throw new Exception("El dueño ya tiene 2 turnos registrados " +
                "para este día");
        }
        
        return turnoRepository.save(turno);
    }
    
    // Sugerir hora disponible
    public LocalDateTime sugerirHoraDisponible(String veterinario, 
                                                LocalDateTime horaInicial) {
        LocalDateTime horaSugerida = horaInicial.plusMinutes(30);
        
        while (true) {
            List<Turno> turnosExistentes = turnoRepository
                .findByVeterinarioAndFechaHoraAndEstado(
                    veterinario, 
                    horaSugerida, 
                    "ACTIVO"
                );
            
            if (turnosExistentes.isEmpty()) {
                return horaSugerida;
            }
            
            horaSugerida = horaSugerida.plusMinutes(30);
        }
    }
    
    // Actualizar turno
    public Turno actualizarTurno(Long id, Turno turnoActualizado) 
        throws Exception {
        Optional<Turno> turnoOpt = turnoRepository.findById(id);
        
        if (turnoOpt.isEmpty()) {
            throw new Exception("Turno no encontrado");
        }
        
        Turno turnoExistente = turnoOpt.get();
        
        if (!"ACTIVO".equals(turnoExistente.getEstado())) {
            throw new Exception("No se puede modificar un turno " + 
                turnoExistente.getEstado().toLowerCase());
        }
        
        // Validar nuevo horario
        turnoActualizado.setId(id);
        turnoActualizado.setEstado("MODIFICADO");
        turnoActualizado.setFechaModificacion(LocalDateTime.now());
        turnoActualizado.setFechaRegistro(turnoExistente.getFechaRegistro());
        
        // Validaciones similares a guardarTurno pero excluyendo el turno actual
        return validarYGuardarActualizacion(turnoActualizado);
    }
    
    private Turno validarYGuardarActualizacion(Turno turno) throws Exception {
        List<Turno> cruceVeterinario = turnoRepository
            .findByVeterinarioAndFechaHoraAndEstado(
                turno.getVeterinario(), 
                turno.getFechaHora(), 
                "ACTIVO"
            );
        
        // Excluir el turno actual de la validación
        cruceVeterinario.removeIf(t -> t.getId().equals(turno.getId()));
        
        if (!cruceVeterinario.isEmpty()) {
            throw new Exception("El nuevo horario ya está ocupado");
        }
        
        return turnoRepository.save(turno);
    }
    
    // Cancelar turno
    public void cancelarTurno(Long id) throws Exception {
        Optional<Turno> turnoOpt = turnoRepository.findById(id);
        
        if (turnoOpt.isEmpty()) {
            throw new Exception("Turno no encontrado");
        }
        
        Turno turno = turnoOpt.get();
        turno.setEstado("CANCELADO");
        turno.setFechaCancelacion(LocalDateTime.now());
        turnoRepository.save(turno);
    }
    
    // Obtener todos los turnos
    public List<Turno> obtenerTodosTurnos() {
        return turnoRepository.findAllByOrderByFechaHoraDesc();
    }
    
    // Obtener turno por ID
    public Optional<Turno> obtenerTurnoPorId(Long id) {
        return turnoRepository.findById(id);
    }
    
    // Obtener turnos cancelados en un rango de fechas
    public List<Turno> obtenerTurnosCancelados(LocalDateTime inicio, 
                                                LocalDateTime fin) {
        return turnoRepository.findTurnosCanceladosByFechaRange(inicio, fin);
    }
    
}
