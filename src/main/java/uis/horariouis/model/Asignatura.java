package uis.horariouis.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Entity
@Table(name = "Asignatura")
public class Asignatura implements Serializable {

    @Serial
    private static final long serialVersionUID = -1234567890L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @CsvBindByName(column = "ID")
    private Long idAsignatura;

    @NotBlank(message = "El código de la asignatura es requerido")
    @CsvBindByName(column = "Código")
    private String codigo;

    @NotBlank(message = "El nombre de la asignatura es requerido")
    @CsvBindByName(column = "Nombre")
    private String nombre;

    @NotNull(message = "Las horas de teoría son requeridas")
    @Positive(message = "Las horas de teoría deben ser un número positivo")
    @CsvBindByName(column = "Horas Teoría")
    private Integer horasTeoria;

    @NotNull(message = "Las horas de práctica son requeridas")
    @Positive(message = "Las horas de práctica deben ser un número positivo")
    @CsvBindByName(column = "Horas Práctica")
    private Integer horasPractica;

}