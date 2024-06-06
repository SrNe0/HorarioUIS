package uis.horariouis.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@Table(name = "rol")
public class Rol implements Serializable {

    @Serial
    private static final long serialVersionUID = -123456789L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRol;

    @NotBlank
    @Size(min = 1, max = 50)
    @Column(name = "nombreRol", unique = true)
    private String nombreRol;
}

