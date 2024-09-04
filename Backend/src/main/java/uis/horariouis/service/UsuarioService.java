package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import uis.horariouis.dto.UsuarioDTO;
import uis.horariouis.model.Rol;
import uis.horariouis.model.Usuario;
import uis.horariouis.repository.RolRepository;
import uis.horariouis.repository.UsuarioRepository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository; // Repositorio para acceder a los datos del usuario en la base de datos
    private final RolRepository rolRepository; // Repositorio para acceder a los roles
    private final BCryptPasswordEncoder passwordEncoder; // Codificador de contraseñas

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
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

    // Guarda un usuario en la base de datos usando el DTO
    public Usuario saveUsuario(UsuarioDTO usuarioDTO) {
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(usuarioDTO.getNombreUsuario());
        usuario.setContrasena(passwordEncoder.encode(usuarioDTO.getContrasena()));

        // Buscar el rol por el nombre proporcionado en el DTO
        Rol rol = (Rol) rolRepository.findByNombreRol(usuarioDTO.getNombreRol())
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + usuarioDTO.getNombreRol()));

        usuario.setRol(rol); // Asigna el rol encontrado

        return usuarioRepository.save(usuario); // Guarda el usuario
    }

    // Método para actualizar el usuario usando el DTO
    public Usuario updateUsuario(Long id, UsuarioDTO usuarioDTO) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar nombre de usuario y contraseña
        usuarioExistente.setNombreUsuario(usuarioDTO.getNombreUsuario());

        if (usuarioDTO.getContrasena() != null && !usuarioExistente.getContrasena().startsWith("$2a$")) {
            usuarioExistente.setContrasena(passwordEncoder.encode(usuarioDTO.getContrasena()));
        }

        // Actualizar el rol si es necesario
        Rol rol = (Rol) rolRepository.findByNombreRol(usuarioDTO.getNombreRol())
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + usuarioDTO.getNombreRol()));
        usuarioExistente.setRol(rol);

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
