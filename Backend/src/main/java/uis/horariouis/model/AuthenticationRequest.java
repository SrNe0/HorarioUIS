package uis.horariouis.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthenticationRequest {

    // Campos para almacenar el nombre de usuario y la contraseña
    private String username;
    private String password;

    // Constructor por defecto para la deserialización de JSON
    public AuthenticationRequest() {
    }

    // Constructor para inicializar los campos username y password
    public AuthenticationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
