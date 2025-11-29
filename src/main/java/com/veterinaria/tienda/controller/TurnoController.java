package com.veterinaria.tienda.controller;

import com.veterinaria.tienda.model.Turno;
import com.veterinaria.tienda.service.TurnoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Controller
public class TurnoController {

    @Autowired
    private TurnoService turnoService;
    
    // Lista de veterinarios disponibles
    private final List<String> veterinarios = Arrays.asList(
        "Dra. López", 
        "Dr. Ramírez", 
        "Dra. Martínez", 
        "Dr. González"
    );
    
    // Lista de especies
    private final List<String> especies = Arrays.asList(
        "Perro", 
        "Gato", 
        "Ave", 
        "Otro"
    );
    
    // Página principal
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    // Página de servicios
    @GetMapping("/servicios")
    public String servicios() {
        return "servicios";
    }
    
    // Formulario de registro de turnos
    @GetMapping("/turnos/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("turno", new Turno());
        model.addAttribute("veterinarios", veterinarios);
        model.addAttribute("especies", especies);
        model.addAttribute("fechaMinima", LocalDate.now());
        return "turno-form";
    }
    
    // Guardar turno
    @PostMapping("/turnos/guardar")
    public String guardarTurno(@Valid @ModelAttribute Turno turno, 
                               BindingResult result, 
                               Model model, 
                               RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("veterinarios", veterinarios);
            model.addAttribute("especies", especies);
            model.addAttribute("fechaMinima", LocalDate.now());
            return "turno-form";
        }
        
        try {
            turnoService.guardarTurno(turno);
            redirectAttributes.addFlashAttribute("mensaje", 
                "Turno registrado exitosamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
            return "redirect:/turnos/lista";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("veterinarios", veterinarios);
            model.addAttribute("especies", especies);
            model.addAttribute("fechaMinima", LocalDate.now());
            return "turno-form";
        }
    }
    
    // Lista de turnos
    @GetMapping("/turnos/lista")
    public String listarTurnos(Model model) {
        List<Turno> turnos = turnoService.obtenerTodosTurnos();
        model.addAttribute("turnos", turnos);
        return "turno-lista";
    }
    
    // Formulario de edición
    @GetMapping("/turnos/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, 
                                           Model model, 
                                           RedirectAttributes redirectAttributes) {
        try {
            Turno turno = turnoService.obtenerTurnoPorId(id)
                .orElseThrow(() -> new Exception("Turno no encontrado"));
            
            model.addAttribute("turno", turno);
            model.addAttribute("veterinarios", veterinarios);
            model.addAttribute("especies", especies);
            model.addAttribute("fechaMinima", LocalDate.now());
            model.addAttribute("edicion", true);
            return "turno-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/turnos/lista";
        }
    }
    
    // Actualizar turno
    @PostMapping("/turnos/actualizar/{id}")
    public String actualizarTurno(@PathVariable Long id, 
                                  @Valid @ModelAttribute Turno turno, 
                                  BindingResult result, 
                                  Model model, 
                                  RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("veterinarios", veterinarios);
            model.addAttribute("especies", especies);
            model.addAttribute("fechaMinima", LocalDate.now());
            model.addAttribute("edicion", true);
            return "turno-form";
        }
        
        try {
            turnoService.actualizarTurno(id, turno);
            redirectAttributes.addFlashAttribute("mensaje", 
                "Turno actualizado exitosamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
            return "redirect:/turnos/lista";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("veterinarios", veterinarios);
            model.addAttribute("especies", especies);
            model.addAttribute("fechaMinima", LocalDate.now());
            model.addAttribute("edicion", true);
            return "turno-form";
        }
    }
    
    // Cancelar turno
    @PostMapping("/turnos/cancelar/{id}")
    public String cancelarTurno(@PathVariable Long id, 
                                RedirectAttributes redirectAttributes) {
        try {
            turnoService.cancelarTurno(id);
            redirectAttributes.addFlashAttribute("mensaje", 
                "Turno cancelado exitosamente");
            redirectAttributes.addFlashAttribute("tipo", "warning");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/turnos/lista";
    }
    
    // Reporte de turnos cancelados
    @GetMapping("/turnos/cancelados")
    public String mostrarTurnosCancelados(@RequestParam(required = false) 
                                          String fechaInicio,
                                          @RequestParam(required = false) 
                                          String fechaFin,
                                          Model model) {
        
        if (fechaInicio != null && fechaFin != null) {
            LocalDateTime inicio = LocalDate.parse(fechaInicio).atStartOfDay();
            LocalDateTime fin = LocalDate.parse(fechaFin).atTime(23, 59, 59);
            
            List<Turno> turnosCancelados = 
                turnoService.obtenerTurnosCancelados(inicio, fin);
            model.addAttribute("turnosCancelados", turnosCancelados);
            model.addAttribute("fechaInicio", fechaInicio);
            model.addAttribute("fechaFin", fechaFin);
        }
        
        return "turnos-cancelados";
    }
    
}
