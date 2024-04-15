package uis.horariouis.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Data
@Entity
@Table(name = "Asignatura")
public class Asignatura implements Serializable {

    @Serial
    private static final long serialVersionUID = -1234567890L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAsignatura;

    @NotBlank(message = "El código de la asignatura es requerido")
    private String codigo;

    @NotBlank(message = "El nombre de la asignatura es requerido")
    private String nombre;

    @NotNull(message = "Las horas de teoría son requeridas")
    private Integer horasTeoria;

    @NotNull(message = "Las horas de práctica son requeridas")
    private Integer horasPractica;

}