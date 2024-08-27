package uis.horariouis.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Time;

@Setter
@Getter
public class Gen {

    private Grupo grupo;
    private Profesor profesor;
    private Aula aula;
    private String dia;
    private Time horaInicio;
    private Time horaFin;

    // Constructor
    public Gen(Grupo grupo, Profesor profesor, Aula aula, String dia, Time horaInicio, Time horaFin) {
        this.grupo = grupo;
        this.profesor = profesor;
        this.aula = aula;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    @Override
    public String toString() {
        return "Gen{" +
                "grupo=" + grupo.getNombreGrupo() +
                ", profesor=" + profesor.getNombreCompleto() +
                ", aula=" + aula.getCodigo() +
                ", dia='" + dia + '\'' +
                ", horaInicio=" + horaInicio +
                ", horaFin=" + horaFin +
                '}';
    }
}
