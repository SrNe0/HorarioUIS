package uis.horariouis.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Time;

@Data
@Entity
@Table(name = "Horario")
public class Horario implements Serializable {
    @Serial
    private static final long serialVersionUID = -1234567890L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHorario;

    @NotNull(message = "El ID del profesor es requerido")
    @ManyToOne
    @JoinColumn(name = "idProfesor", referencedColumnName = "idUsuario")
    private Profesor profesor;

    @NotNull(message = "El ID del grupo es requerido")
    @ManyToOne
    @JoinColumn(name = "idGrupo", referencedColumnName = "idGrupo")
    private Grupo grupo;

    @NotNull(message = "El ID del aula es requerido")
    @ManyToOne
    @JoinColumn(name = "idAula", referencedColumnName = "idAula")
    private Aula aula;

    @NotBlank(message = "El día es requerido")
    private String dia;

    @NotNull(message = "La hora de inicio es requerida")
    private Time horaInicio;

    @NotNull(message = "La hora de fin es requerida")
    private Time horaFin;

    // Constructor vacío
    public Horario() {
    }

    // Constructor con todos los campos
    public Horario(Long idHorario, Profesor profesor, Grupo grupo, Aula aula, String dia, Time horaInicio, Time horaFin) {
        this.idHorario = idHorario;
        this.profesor = profesor;
        this.grupo = grupo;
        this.aula = aula;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }



    // Getters y setters
    // getIdHorario(), setIdHorario(), getProfesor(), setProfesor(), getGrupo(), setGrupo(), getAula(), setAula(), getDia(), setDia(), getHoraInicio(), setHoraInicio(), getHoraFin(), setHoraFin(), etc.
}
