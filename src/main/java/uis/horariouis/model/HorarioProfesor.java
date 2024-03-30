package uis.horariouis.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@Table(name = "HorarioProfesor")
public class HorarioProfesor implements Serializable {
    @Serial
    private static final long serialVersionUID = -1234567890L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHorarioProfesor;

    @NotNull(message = "El ID del profesor es requerido")
    @ManyToOne
    @JoinColumn(name = "idProfesor", referencedColumnName = "idUsuario")
    private Profesor profesor;

    @NotNull(message = "El ID de la disponibilidad horaria es requerido")
    @ManyToOne
    @JoinColumn(name = "idDisponibilidad", referencedColumnName = "idDisponibilidad")
    private DisponibilidadHoraria disponibilidadHoraria;

    // Constructor vacío
    public HorarioProfesor() {
    }

    // Constructor con todos los campos
    public HorarioProfesor(Long idHorarioProfesor, Profesor profesor, DisponibilidadHoraria disponibilidadHoraria) {
        this.idHorarioProfesor = idHorarioProfesor;
        this.profesor = profesor;
        this.disponibilidadHoraria = disponibilidadHoraria;
    }

    // Getters y setters
    // getIdHorarioProfesor(), setIdHorarioProfesor(), getProfesor(), setProfesor(), getDisponibilidadHoraria(), setDisponibilidadHoraria(), etc.
}
