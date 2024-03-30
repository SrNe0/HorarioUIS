package uis.horariouis.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@Table(name = "Aula")
public class Aula implements Serializable {

    @Serial
    private static final long serialVersionUID = -4567890123L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAula")
    private Long idAula;

    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "codigo", unique = true)
    private String codigo;

    @NotBlank
    @Size(min = 1, max = 100)
    @Column(name = "descripcion")
    private String descripcion;

    @Min(1)
    @Column(name = "capacidad")
    private int capacidad;

    @ManyToOne
    @JoinColumn(name = "idEdificio", referencedColumnName = "idEdificio")
    private Edificio edificio;
}
