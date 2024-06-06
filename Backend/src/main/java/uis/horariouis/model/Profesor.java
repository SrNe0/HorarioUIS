package uis.horariouis.model;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@Table(name = "profesor")
public class Profesor implements Serializable {
    @Serial
    private static final long serialVersionUID = -123456789L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProfesor;

    @NotBlank(message = "El documento de identidad es requerido")
    private String documentoIdentidad;

    @NotBlank(message = "El primer apellido es requerido")
    @Column(name = "apellido1")
    private String apellido1;

    @NotBlank(message = "El segundo apellido es requerido")
    @Column(name = "apellido2")
    private String apellido2;

    @NotBlank(message = "El primer nombre es requerido")
    @Column(name = "nombre1")
    private String nombre1;

    @Column(name = "nombre2")
    private String nombre2;

    @NotBlank(message = "El número de teléfono es requerido")
    private String telefono;

    @NotBlank(message = "El correo electrónico es requerido")
    private String correo;

    // Referencia opcional a Usuario si es necesario para la lógica de negocio
    @OneToOne
    @JoinColumn(name = "idUsuario", referencedColumnName = "idUsuario")
    private Usuario usuario;

    // Constructor, getters y setters
}
