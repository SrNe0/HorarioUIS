package uis.horariouis.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Entity
@Table(name = "RolAccion")
public class RolAccion implements Serializable {

    private static final long serialVersionUID = -1234567890L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRolAccion;

    @NotNull(message = "El ID del rol es requerido")
    @ManyToOne
    @JoinColumn(name = "idRol", referencedColumnName = "idRol")
    private Rol rol;

    @NotNull(message = "El ID de la acción es requerido")
    @ManyToOne
    @JoinColumn(name = "idAccion", referencedColumnName = "idAccion")
    private Accion accion;

    // Constructor vacío
    public RolAccion() {
    }

    // Constructor con todos los campos
    public RolAccion(Long idRolAccion, Rol rol, Accion accion) {
        this.idRolAccion = idRolAccion;
        this.rol = rol;
        this.accion = accion;
    }

    // Getters y setters
    // getIdRolAccion(), setIdRolAccion(), getRol(), setRol(), getAccion(), setAccion(), etc.
}
