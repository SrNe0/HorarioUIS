package uis.horariouis.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthenticationResponse {

    // Campo para almacenar el token JWT
    private String jwt;

    // Constructor para inicializar el campo jwt
    public AuthenticationResponse(String jwt) {
        this.jwt = jwt;
    }

}
