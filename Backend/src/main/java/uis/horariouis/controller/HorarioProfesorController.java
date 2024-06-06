package uis.horariouis.controller;

// Importaciones de Spring para manejar la inyección de dependencias, respuestas HTTP y anotaciones de mapeo en el controlador.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// Importación de la clase de excepción personalizada y del modelo 'HorarioProfesor'.
import uis.horariouis.exception.ResourceNotFoundException;
import uis.horariouis.model.HorarioProfesor;
// Importación del servicio relacionado con la gestión de horarios de profesores.
import uis.horariouis.service.HorarioProfesorService;

import javax.validation.Valid;
import java.util.List;

// Anotación que define esta clase como un controlador REST y mapea las solicitudes a la ruta base para horarios de profesores.
@RestController
@RequestMapping("/api/horarios-profesores")
public class HorarioProfesorController {

    // Inyección automática del servicio 'HorarioProfesorService' por Spring.
    @Autowired
    private HorarioProfesorService horarioProfesorService;

    // Método para obtener todos los horarios de profesores disponibles, mapeado a una petición GET.
    @GetMapping
    public List<HorarioProfesor> getAllHorariosProfesores() {
        return horarioProfesorService.getAllHorariosProfesores();
    }

    // Método para obtener un horario de profesor por su ID, mapeado a GET con un parámetro de ruta.
    @GetMapping("/{id}")
    public ResponseEntity<HorarioProfesor> getHorarioProfesorById(@PathVariable Long id) {
        // Se intenta obtener el horario de profesor por su ID y se maneja la excepción si no se encuentra.
        HorarioProfesor horarioProfesor = horarioProfesorService.getHorarioProfesorById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario profesor not found with id: " + id));
        return ResponseEntity.ok(horarioProfesor);
    }

    // Método para crear un nuevo horario de profesor, mapeado a POST.
    @PostMapping
    public ResponseEntity<?> createHorarioProfesor(@Valid @RequestBody HorarioProfesor horarioProfesor) {
        // Se guarda el nuevo horario de profesor y se devuelve con estado HTTP 201 si se creó correctamente.
        HorarioProfesor nuevoHorarioProfesor = horarioProfesorService.saveHorarioProfesor(horarioProfesor);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoHorarioProfesor);
    }

    // Método para actualizar un horario de profesor existente, mapeado a PUT con un parámetro de ruta.
    @PutMapping("/{id}")
    public ResponseEntity<HorarioProfesor> updateHorarioProfesor(@PathVariable Long id, @Valid @RequestBody HorarioProfesor horarioProfesor) {
        // Se intenta actualizar el horario de profesor por su ID y se maneja la excepción si no se encuentra.
        HorarioProfesor horarioProfesorActualizado = horarioProfesorService.getHorarioProfesorById(id)
                .map(horario -> {
                    // Se actualizan los datos del horario de profesor con los nuevos datos recibidos.
                    horario.setProfesor(horarioProfesor.getProfesor());
                    horario.setDisponibilidadHoraria(horarioProfesor.getDisponibilidadHoraria());
                    return horarioProfesorService.saveHorarioProfesor(horario);
                }).orElseThrow(() -> new ResourceNotFoundException("Horario profesor not found with id: " + id));

        return ResponseEntity.ok(horarioProfesorActualizado);
    }

    // Método para eliminar un horario de profesor por su ID, mapeado a DELETE.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHorarioProfesor(@PathVariable Long id) {
        // Se elimina el horario de profesor por su ID y se devuelve una respuesta sin contenido (204 No Content).
        horarioProfesorService.deleteHorarioProfesorById(id);
        return ResponseEntity.noContent().build();
    }
}
