package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.model.Profesor;
import uis.horariouis.model.Rol;
import uis.horariouis.model.Usuario;
import uis.horariouis.repository.ProfesorRepository;
import uis.horariouis.repository.RolRepository;
import uis.horariouis.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProfesorService {

    private final ProfesorRepository profesorRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    @Autowired
    public ProfesorService(ProfesorRepository profesorRepository, UsuarioRepository usuarioRepository, RolRepository rolRepository) {
        this.profesorRepository = profesorRepository;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    public List<Profesor> getAllProfesores() {
        return profesorRepository.findAll();
    }

    public Optional<Profesor> getProfesorById(Long id) {
        return profesorRepository.findById(id);
    }

    public Profesor createProfesor(Profesor profesor) {
        // Generar nombre de usuario y contraseña
        String nombreUsuario = profesor.getApellido2() + profesor.getNombre1();
        String contrasena = profesor.getDocumentoIdentidad();

        // Obtener el rol correspondiente (User)
        Optional<Rol> optionalRol = rolRepository.findById(2L); // El ID del rol User es 2
        Rol rol = optionalRol.orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        // Crear el usuario asociado al profesor
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(nombreUsuario);
        usuario.setContrasena(contrasena);
        usuario.setRol(rol);
        usuarioRepository.save(usuario);

        // Asignar el usuario al profesor
        profesor.setUsuario(usuario);

        return profesorRepository.save(profesor);
    }

    public Profesor updateProfesor(Long id, Profesor profesorDetails) {
        Profesor profesor = profesorRepository.findById(id).orElseThrow(() -> new RuntimeException("Profesor no encontrado"));
        profesor.setDocumentoIdentidad(profesorDetails.getDocumentoIdentidad());
        profesor.setApellido1(profesorDetails.getApellido1());
        profesor.setApellido2(profesorDetails.getApellido2());
        profesor.setNombre1(profesorDetails.getNombre1());
        profesor.setNombre2(profesorDetails.getNombre2());
        profesor.setTelefono(profesorDetails.getTelefono());
        profesor.setCorreo(profesorDetails.getCorreo());
        return profesorRepository.save(profesor);
    }

    public void deleteProfesor(Long id) {
        profesorRepository.deleteById(id);
    }
}

