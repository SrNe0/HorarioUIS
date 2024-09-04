package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import uis.horariouis.model.Usuario;
import uis.horariouis.repository.UsuarioRepository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service // Marca esta clase como un servicio gestionado por Spring
public class UsuarioService {

    private final UsuarioRepository usuarioRepository; // Repositorio para acceder a los datos del usuario en la base de datos
    private final BCryptPasswordEncoder passwordEncoder; // Codificador de contraseñas

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Obtiene todos los usuarios
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    // Obtiene un usuario por su ID
    public Optional<Usuario> getUsuarioById(Long id) {
        return usuarioRepository.findById(id);
    }

    // Obtiene un usuario por su nombre de usuario
    public Usuario findByNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario);
    }

    // Guarda un usuario en la base de datos
    public Usuario saveUsuario(Usuario usuario) {
        // Verificar si la contraseña ya está encriptada
        if (usuario.getContrasena() != null && !usuario.getContrasena().startsWith("$2a$")) {
            usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena())); // Encriptar si no está encriptada
        }
        return usuarioRepository.save(usuario); // Guardar el usuario con la contraseña encriptada
    }

    // Guarda un usuario en la base de datos encriptando la contraseña explícitamente (opcional si no se usa el anterior)
    public void saveUsuarioConEncriptacion(Usuario usuario) {
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        usuarioRepository.save(usuario);
    }

    // Método para actualizar el usuario
    public Usuario updateUsuario(Long id, Usuario usuarioActualizado) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar si la contraseña ha cambiado y encriptarla
        if (usuarioActualizado.getContrasena() != null &&
                !usuarioActualizado.getContrasena().startsWith("$2a$") &&
                !usuarioActualizado.getContrasena().equals(usuarioExistente.getContrasena())) {
            usuarioExistente.setContrasena(passwordEncoder.encode(usuarioActualizado.getContrasena()));
        }

        // Actualizar otros campos (según sea necesario)
        usuarioExistente.setNombreUsuario(usuarioActualizado.getNombreUsuario());
        // Agregar aquí la actualización de otros campos...

        // Guardar el usuario actualizado
        return usuarioRepository.save(usuarioExistente);
    }

    // Elimina un usuario por su ID
    public void deleteUsuarioById(Long id) {
        usuarioRepository.deleteById(id);
    }

    // Encripta todas las contraseñas de los usuarios que no están encriptadas
    @PostConstruct
    public void encryptAllPasswords() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        for (Usuario usuario : usuarios) {
            if (!usuario.getContrasena().startsWith("$2a$")) { // Verificar si ya está encriptada
                usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
                usuarioRepository.save(usuario);
            }
        }
    }
}
