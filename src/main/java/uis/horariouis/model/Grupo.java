package uis.horariouis.model;

import com.opencsv.bean.CsvBindByName;
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
    @CsvBindByName(column = "IDGRUPO")
    @Column(name = "idgrupo")
    private Long idGrupo;

    @NotNull(message = "La asignatura es requerida")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idasignatura", referencedColumnName = "idasignatura")
    @CsvBindByName(column = "ASIGNATURA")
    private Asignatura asignatura;

    @NotBlank(message = "El nombre del grupo es requerido")
    @CsvBindByName(column = "NOMBREGRUPO")
    @Column(name = "nombregrupo")
    private String nombreGrupo;

    @CsvBindByName(column = "CUPO")
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

