package uis.horariouis.model;

import lombok.Getter;
import lombok.Setter;
import uis.horariouis.model.Rol;

@Setter
@Getter
public class AuthenticationResponse {

    // Campo para almacenar el token JWT
    private String jwt;

    // Rol del usuario autenticado
    private Rol rol;

    // Indicador de si el token expira
    private boolean expires;

    // Tiempo en segundos de duración del token
    private int timeout;

    // Constructor para inicializar todos los campos
    public AuthenticationResponse(String jwt, Rol rol, boolean expires, int timeout) {
        this.jwt = jwt;
        this.rol = rol;
        this.expires = expires;
        this.timeout = timeout;
    }
}
