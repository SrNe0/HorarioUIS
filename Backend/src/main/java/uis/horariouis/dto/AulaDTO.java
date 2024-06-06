package uis.horariouis.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AulaDTO {
    private String codigo;
    private String descripcion;
    private int capacidad;
    private String nombreEdificio; // Usamos el nombre del edificio en lugar del ID

    // Getters y Setters

}
