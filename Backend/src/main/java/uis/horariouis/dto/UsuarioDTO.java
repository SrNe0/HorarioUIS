package uis.horariouis.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UsuarioDTO {
    private String nombreUsuario;
    private String contrasena;
    private String nombreRol;  // Aquí solo pedimos el nombre del rol, no su ID
}
