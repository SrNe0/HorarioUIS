package uis.horariouis.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serial;

@Data
@Entity
@Table(name = "Grupo")
public class Grupo {

    @Serial
    private static final long serialVersionUID = -2345678901L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idgrupo")
    private int idGrupo;

    @NotNull(message = "La asignatura es requerida")
    @ManyToOne
    @JoinColumn(name = "idasignatura", referencedColumnName = "idasignatura")
    private Asignatura asignatura;

    @NotBlank(message = "El nombre del grupo es requerido")
    @Column(name = "nombregrupo")
    private String nombreGrupo;

    @Column(name = "cupo")
    private int cupo;
    // Constructor vacío
    public Grupo() {
    }

    // Constructor con todos los campos
    public Grupo(String nombreGrupo, int cupo, Asignatura asignatura) {
        this.nombreGrupo = nombreGrupo;
        this.cupo = cupo;
        this.asignatura = asignatura;
    }

    // Getters y setters
    // getIdGrupo(), setIdGrupo(), getNombreGrupo(), setNombreGrupo(), getCupo(), setCupo(), getAsignatura(), setAsignatura(), etc.

}

