package uis.horariouis.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
public class Profesor extends Usuario implements Serializable {
    @Serial
    private static final long serialVersionUID = -1234567890L;

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

    // Constructor vacío
    public Profesor() {
    }

    // Constructor con todos los campos
    public Profesor(String documentoIdentidad, String apellidos, String nombres, String telefono, String correo) {
        this.documentoIdentidad = documentoIdentidad;
        this.apellidos = apellidos;
        this.nombres = nombres;
        this.telefono = telefono;
        this.correo = correo;
    }

    // Getters y setters adicionales si es necesario
    // getDocumentoIdentidad(), setDocumentoIdentidad(), etc.
}
