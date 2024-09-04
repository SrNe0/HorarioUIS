package uis.horariouis.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class AsignaturaDTO {

    @NotBlank(message = "El código de la asignatura es requerido")
    private String codigo;

    @NotBlank(message = "El nombre de la asignatura es requerido")
    private String nombre;

    @NotNull(message = "Las horas de teoría son requeridas")
    @Positive(message = "Las horas de teoría deben ser un número positivo")
    private Integer horasTeoria;

    @NotNull(message = "Las horas de práctica son requeridas")
    @Positive(message = "Las horas de práctica deben ser un número positivo")
    private Integer horasPractica;

    @NotNull(message = "La necesidad de computadores es requerida")
    private Boolean necesitaComputadores;
}
