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

    // Guarda un usuario en la base de datos sin encriptar la contraseña
    public Usuario saveUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
        return usuario;
    }

    // Guarda un usuario en la base de datos encriptando la contraseña
    public void saveUsuarioConEncriptacion(Usuario usuario) {
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        usuarioRepository.save(usuario);
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
