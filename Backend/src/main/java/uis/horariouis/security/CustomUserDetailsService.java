package uis.horariouis.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uis.horariouis.model.Usuario;
import uis.horariouis.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service // Marca esta clase como un servicio gestionado por Spring
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class); // Logger para registrar mensajes

    @Autowired
    private UsuarioRepository usuarioRepository; // Repositorio para acceder a los datos del usuario en la base de datos

    @Override
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
        logger.info("Loading user by username: {}", nombreUsuario); // Registra el intento de carga del usuario
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario);
        if (usuario == null) {
            logger.error("User not found: {}", nombreUsuario); // Registra un error si el usuario no se encuentra
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        logger.info("User found: {}", nombreUsuario); // Registra que el usuario fue encontrado
        return new CustomUserDetails(usuario); // Retorna los detalles del usuario envueltos en un objeto CustomUserDetails
    }

    // Método adicional para cargar un usuario directamente desde el repositorio
    public Usuario loadUsuarioByUsername(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario);
    }
}
