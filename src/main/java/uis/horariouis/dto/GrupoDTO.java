package uis.horariouis.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GrupoDTO {
    // Setter para codigoAsignatura
    // Getter para codigoAsignatura
    private String codigoAsignatura;  // Atributo para el código de la asignatura
    // Setter para nombreGrupo
    // Getter para nombreGrupo
    private String nombreGrupo;  // Atributo para el nombre del grupo

    // Constructor por defecto
    public GrupoDTO() {
    }

    // Constructor con todos los atributos
    public GrupoDTO(String codigoAsignatura, String nombreGrupo) {
        this.codigoAsignatura = codigoAsignatura;
        this.nombreGrupo = nombreGrupo;
    }

    // Métodos adicionales si son necesarios...
}