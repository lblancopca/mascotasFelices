package com.veterinaria.tienda.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "turnos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Turno {

     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre del animal es obligatorio")
    @Column(nullable = false)
    private String nombreAnimal;
    
    @NotBlank(message = "La especie es obligatoria")
    @Column(nullable = false)
    private String especie;
    
    @NotBlank(message = "El nombre del dueño es obligatorio")
    @Column(nullable = false)
    private String nombreDueno;
    
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "\\d{10}", message = "El teléfono debe tener 10 dígitos")
    @Column(nullable = false)
    private String telefono;
    
    @NotBlank(message = "El veterinario es obligatorio")
    @Column(nullable = false)
    private String veterinario;
    
    @NotNull(message = "La fecha y hora son obligatorias")
    @Future(message = "La fecha debe ser futura")
    @Column(nullable = false)
    private LocalDateTime fechaHora;
    
    @Column(nullable = false)
    private String estado = "ACTIVO"; // ACTIVO, MODIFICADO, CANCELADO
    
    @Column
    private LocalDateTime fechaModificacion;
    
    @Column
    private LocalDateTime fechaCancelacion;
    
    @Column
    private LocalDateTime fechaRegistro;
    
    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
    
}
