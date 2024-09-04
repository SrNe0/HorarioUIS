package uis.horariouis.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class ProfesorDTO {

    @NotBlank(message = "El documento de identidad es requerido")
    private String documentoIdentidad;

    @NotBlank(message = "El primer apellido es requerido")
    private String apellido1;

    private String apellido2;

    @NotBlank(message = "El primer nombre es requerido")
    private String nombre1;

    private String nombre2;

    @NotBlank(message = "El número de teléfono es requerido")
    @Pattern(regexp = "^\\d{7,10}$", message = "El número de teléfono debe tener entre 7 y 10 dígitos")
    private String telefono;

    @NotBlank(message = "El correo electrónico es requerido")
    @Email(message = "Debe ser un correo electrónico válido")
    private String correo;
}
