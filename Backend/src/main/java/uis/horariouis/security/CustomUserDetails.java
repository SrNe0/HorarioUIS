package uis.horariouis.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uis.horariouis.model.Usuario;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final Usuario usuario; // Instancia del usuario para obtener sus detalles

    // Constructor que recibe una instancia de Usuario
    public CustomUserDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    // Devuelve el ID del usuario
    public Long getId() {
        return usuario.getIdUsuario();
    }

    // Devuelve la colección de autoridades otorgadas al usuario (roles)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Crea una lista de autoridades con el rol del usuario
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombreRol()));
    }

    // Devuelve la contraseña del usuario
    @Override
    public String getPassword() {
        return usuario.getContrasena();
    }

    // Devuelve el nombre de usuario
    @Override
    public String getUsername() {
        return usuario.getNombreUsuario();
    }

    // Indica si la cuenta del usuario no ha expirado (siempre true en este caso)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Indica si la cuenta del usuario no está bloqueada (siempre true en este caso)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Indica si las credenciales del usuario no han expirado (siempre true en este caso)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Indica si la cuenta del usuario está habilitada (siempre true en este caso)
    @Override
    public boolean isEnabled() {
        return true;
    }
}
