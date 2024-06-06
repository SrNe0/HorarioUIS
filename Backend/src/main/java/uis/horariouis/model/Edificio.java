package uis.horariouis.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;

import com.opencsv.bean.CsvBindByName;

@Data
@Entity
@Table(name = "Edificio")
public class Edificio implements Serializable {

    @Serial
    private static final long serialVersionUID = -3456789012L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idedificio")
    @CsvBindByName(column = "ID")
    private Long idEdificio;

    @NotBlank(message = "El nombre del edificio es requerido")
    @Column(name = "nombre")
    @CsvBindByName(column = "Nombre")
    private String nombre;

}
