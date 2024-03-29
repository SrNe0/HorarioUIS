package uis.horariouis.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

@Data
@Entity
@Table(name = "horario")
public class Horario implements Serializable {

    @Serial
    private static final long serialVersionUID = -987654321L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHorario;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "idGrupo", referencedColumnName = "idGrupo")
    private Grupo grupo;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "idAula", referencedColumnName = "idAula")
    private Aula aula;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "idPersonal", referencedColumnName = "idPersonal")
    private Personal personal;

    @NotNull
    @Column(name = "fecha")
    private Date fecha;

    @NotNull
    @Column(name = "horaInicio")
    private Time horaInicio;

    @NotNull
    @Column(name = "horaFin")
    private Time horaFin;
}

