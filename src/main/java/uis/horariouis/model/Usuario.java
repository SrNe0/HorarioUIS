package uis.horariouis.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Entity
@Table(name = "Usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = -1234567890L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @NotBlank(message = "El nombre de usuario es requerido")
    private String nombreUsuario;

    @NotBlank(message = "La contraseña es requerida")
    private String contrasena;

    @NotNull(message = "El rol es requerido")
    @ManyToOne
    @JoinColumn(name = "idRol", referencedColumnName = "idRol")
    private Rol rol;

    // Constructor vacío
    public Usuario() {
    }

    // Constructor con todos los campos
    public Usuario(Long idUsuario, String nombreUsuario, String contrasena, Rol rol) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    // Getters y setters
    // getIdUsuario(), setIdUsuario(), getNombreUsuario(), setNombreUsuario(), getContrasena(), setContrasena(), getRol(), setRol(), etc.
}
