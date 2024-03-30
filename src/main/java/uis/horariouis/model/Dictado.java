package uis.horariouis.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@Table(name = "Dictado")
public class Dictado implements Serializable {

    @Serial
    private static final long serialVersionUID = -1234567890L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDictado;

    @NotNull(message = "El ID del profesor es requerido")
    @ManyToOne
    @JoinColumn(name = "idProfesor", referencedColumnName = "idUsuario")
    private Profesor profesor;

    @NotNull(message = "El ID de la asignatura es requerido")
    @ManyToOne
    @JoinColumn(name = "idAsignatura", referencedColumnName = "idAsignatura")
    private Asignatura asignatura;

    // Constructor vacío
    public Dictado() {
    }

    // Constructor con todos los campos
    public Dictado(Long idDictado, Profesor profesor, Asignatura asignatura) {
        this.idDictado = idDictado;
        this.profesor = profesor;
        this.asignatura = asignatura;
    }

    // Getters y setters
    // getIdDictado(), setIdDictado(), getProfesor(), setProfesor(), getAsignatura(), setAsignatura(), etc.
}
