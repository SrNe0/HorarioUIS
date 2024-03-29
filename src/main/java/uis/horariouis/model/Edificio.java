package uis.horariouis.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@Table(name = "Edificio")
public class Edificio implements Serializable {

    @Serial
    private static final long serialVersionUID = -3456789012L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idedificio")
    private int idEdificio;

    @NotBlank(message = "El nombre del edificio es requerido")
    @Column(name = "nombre")
    private String nombre;

}
