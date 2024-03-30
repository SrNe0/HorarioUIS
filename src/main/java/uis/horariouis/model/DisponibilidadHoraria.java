package uis.horariouis.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Time;

@Data
@Entity
@Table(name = "DisponibilidadHoraria")
public class DisponibilidadHoraria implements Serializable {

    private static final long serialVersionUID = -1234567890L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDisponibilidad;

    @NotBlank(message = "El día de disponibilidad es requerido")
    private String dia;

    @NotNull(message = "La hora de inicio es requerida")
    private Time horaInicio;

    @NotNull(message = "La hora de fin es requerida")
    private Time horaFin;

    // Constructor vacío
    public DisponibilidadHoraria() {
    }

    // Constructor con todos los campos
    public DisponibilidadHoraria(Long idDisponibilidad, String dia, Time horaInicio, Time horaFin) {
        this.idDisponibilidad = idDisponibilidad;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    // Getters y setters
    // getIdDisponibilidad(), setIdDisponibilidad(), getDia(), setDia(), getHoraInicio(), setHoraInicio(), getHoraFin(), setHoraFin(), etc.
}
