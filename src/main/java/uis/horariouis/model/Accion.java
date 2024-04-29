package uis.horariouis.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@Table(name = "Accion")
public class Accion implements Serializable {

    @Serial
    private static final long serialVersionUID = -1234567890L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAccion;

    @NotBlank(message = "El nombre de la acción es requerido")
    private String nombreAccion;

    // Constructor vacío
    public Accion() {
    }

    // Constructor con todos los campos
    public Accion(Long idAccion, String nombreAccion) {
        this.idAccion = idAccion;
        this.nombreAccion = nombreAccion;
    }

    // Getters y setters
    // getIdAccion(), setIdAccion(), getNombreAccion(), setNombreAccion(), etc.
}
