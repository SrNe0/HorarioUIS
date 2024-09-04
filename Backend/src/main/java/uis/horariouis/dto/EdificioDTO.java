package uis.horariouis.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class EdificioDTO {

    @NotBlank(message = "El nombre del edificio es requerido")
    private String nombre;  // Solo se incluye el nombre del edificio, no el ID
}
