package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import uis.horariouis.dto.ProfesorDTO;
import uis.horariouis.exception.ResourceNotFoundException;
import uis.horariouis.model.*;
import uis.horariouis.repository.*;

import java.sql.Time;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ProfesorService {
    @Autowired
    private DictadoRepository dictadoRepository;
    private final Random random = new Random();

    private final ProfesorRepository profesorRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public ProfesorService(ProfesorRepository profesorRepository, UsuarioRepository usuarioRepository, RolRepository rolRepository, BCryptPasswordEncoder passwordEncoder) {
        this.profesorRepository = profesorRepository;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Obtener todos los profesores
    public List<Profesor> getAllProfesores() {
        return profesorRepository.findAll();
    }

    // Obtener un profesor por ID
    public Optional<Profesor> getProfesorById(Long id) {
        return profesorRepository.findById(id);
    }

    // Crear un nuevo profesor usando el DTO
    public Profesor createProfesor(ProfesorDTO profesorDTO) {
        // Generar nombre de usuario y contraseña
        String nombreUsuario = profesorDTO.getApellido2() + profesorDTO.getNombre1();
        String contrasena = profesorDTO.getDocumentoIdentidad();

        // Obtener el rol correspondiente (User)
        Rol rol = rolRepository.findById(2L) // El ID del rol User es 2
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));

        // Crear el usuario asociado al profesor
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(nombreUsuario);
        usuario.setContrasena(passwordEncoder.encode(contrasena)); // Encriptar la contraseña
        usuario.setRol(rol);
        usuarioRepository.save(usuario);

        // Crear el profesor y asignar el usuario
        Profesor profesor = new Profesor();
        profesor.setDocumentoIdentidad(profesorDTO.getDocumentoIdentidad());
        profesor.setApellido1(profesorDTO.getApellido1());
        profesor.setApellido2(profesorDTO.getApellido2());
        profesor.setNombre1(profesorDTO.getNombre1());
        profesor.setNombre2(profesorDTO.getNombre2());
        profesor.setTelefono(profesorDTO.getTelefono());
        profesor.setCorreo(profesorDTO.getCorreo());
        profesor.setUsuario(usuario); // Asignar el usuario creado

        // Guardar el profesor en la base de datos
        return profesorRepository.save(profesor);
    }

    // Actualizar un profesor existente usando el DTO
    public Profesor updateProfesor(Long id, ProfesorDTO profesorDTO) {
        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado con id: " + id));

        // Actualizar los campos del profesor
        profesor.setDocumentoIdentidad(profesorDTO.getDocumentoIdentidad());
        profesor.setApellido1(profesorDTO.getApellido1());
        profesor.setApellido2(profesorDTO.getApellido2());
        profesor.setNombre1(profesorDTO.getNombre1());
        profesor.setNombre2(profesorDTO.getNombre2());
        profesor.setTelefono(profesorDTO.getTelefono());
        profesor.setCorreo(profesorDTO.getCorreo());

        return profesorRepository.save(profesor);
    }

    // Eliminar un profesor por ID
    public void deleteProfesor(Long id) {
        if (!profesorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Profesor no encontrado con id: " + id);
        }
        profesorRepository.deleteById(id);
    }
    // Método para seleccionar un profesor adecuado según la asignatura del grupo
    public Profesor obtenerProfesorAdecuado(Grupo grupo) {
        List<Dictado> dictados = dictadoRepository.findByAsignatura_IdAsignatura(grupo.getAsignatura().getIdAsignatura());
        List<Profesor> profesoresAdecuados = dictados.stream()
                .map(Dictado::getProfesor)
                .toList();

        if (profesoresAdecuados.isEmpty()) {
            // Si no hay profesores disponibles, devolvemos null en lugar de lanzar una excepción
            return null;
        }

        return profesoresAdecuados.get(random.nextInt(profesoresAdecuados.size()));
    }

    // Verificar si el profesor es adecuado para dictar la asignatura del grupo
    public boolean esProfesorAdecuado(Grupo grupo, Profesor profesor) {
        List<Dictado> dictados = dictadoRepository.findByAsignatura_IdAsignatura(grupo.getAsignatura().getIdAsignatura());
        return dictados.stream().anyMatch(dictado -> dictado.getProfesor().getIdProfesor().equals(profesor.getIdProfesor()));
    }
    @Autowired
    private HorarioRepository horarioRepository;
    @Autowired
    private AulaService aulaService;

    // Método para verificar si un profesor tiene solapamiento en el horario
    public boolean existeSolapamientoProfesor(Profesor profesor, int dia, int horaInicio, int horasDuracion) {
        Time horaInicioPropuesta = Time.valueOf(horaInicio + ":00:00");
        Time horaFinPropuesta = Time.valueOf((horaInicio + horasDuracion) + ":00:00");

        // Consulta para verificar solapamientos de profesor en el mismo día
        List<Horario> horariosSolapados = horarioRepository.findByProfesor_IdProfesorAndDia(
                profesor.getIdProfesor(), aulaService.convertirDia(dia)
        );

        for (Horario horario : horariosSolapados) {
            Time horaInicioExistente = horario.getHoraInicio();
            Time horaFinExistente = horario.getHoraFin();

            // Verifica si hay solapamiento de tiempos para el profesor
            if (horaInicioPropuesta.before(horaFinExistente) && horaFinPropuesta.after(horaInicioExistente)) {
                return true;  // Hay solapamiento
            }
        }
        return false;  // No hay solapamiento
    }

    public Profesor reintentarSolapamientoProfesor(Profesor profesorInicial, int dia, int horaInicio, int horasDuracion, Grupo grupo) {
        Profesor profesor = profesorInicial;
        int intentos = 0;

        // Intentar hasta 10 veces cambiar de profesor o reprogramar la hora
        while (existeSolapamientoProfesor(profesor, dia, horaInicio, horasDuracion) && intentos < 10) {
            profesor = obtenerProfesorAdecuado(grupo);  // Cambiar de profesor si hay solapamiento

            // Cambiar a otro día y hora si persiste el solapamiento
            dia = random.nextInt(6) + 1;  // Cambiar a un día diferente (1 a 6, lunes a sábado)
            horaInicio = random.nextInt(14) + 6;  // Cambiar a una nueva hora (de 6 a 20)

            intentos++;
        }

        // Si después de 10 intentos no se encuentra una solución, devolver null
        if (existeSolapamientoProfesor(profesor, dia, horaInicio, horasDuracion)) {
            return null;
        }

        return profesor;
    }



}

