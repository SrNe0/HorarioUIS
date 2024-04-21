package uis.horariouis.model;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@Entity
@Table(name = "profesor")
public class Profesor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProfesor;

    @NotBlank(message = "El documento de identidad es requerido")
    private String documentoIdentidad;

    @NotBlank(message = "Los apellidos son requeridos")
    private String apellidos;

    @NotBlank(message = "Los nombres son requeridos")
    private String nombres;

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
