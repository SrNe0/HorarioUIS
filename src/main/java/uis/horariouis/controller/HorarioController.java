package uis.horariouis.controller;

// Importaciones de Spring para manejar la inyección de dependencias, respuestas HTTP y anotaciones de mapeo en el controlador.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// Importaciones del modelo 'Horario' y del servicio 'HorarioService' para la gestión de los horarios.
import uis.horariouis.model.Horario;
import uis.horariouis.service.HorarioService;

import java.util.List;
import java.util.Optional;

// Anotación que define esta clase como un controlador REST y mapea las solicitudes a la ruta base para horarios.
@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    private final HorarioService horarioService;

    // Inyección de dependencia del servicio 'HorarioService' en el constructor del controlador.
    @Autowired
    public HorarioController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    // Método para obtener todos los horarios disponibles, mapeado a una petición GET.
    @GetMapping
    public List<Horario> getAllHorarios() {
        return horarioService.getAllHorarios();
    }

    // Método para obtener un horario por su ID, mapeado a GET con un parámetro de ruta.
    @GetMapping("/{id}")
    public ResponseEntity<Horario> getHorarioById(@PathVariable Long id) {
        Optional<Horario> horario = horarioService.getHorarioById(id);
        return horario.map(value -> ResponseEntity.ok().body(value))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Método para crear un nuevo horario, mapeado a POST.
    @PostMapping
    public ResponseEntity<Horario> createHorario(@RequestBody Horario horario) {
        Horario createdHorario = horarioService.createHorario(horario);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdHorario);  // Retorna el horario creado con estado HTTP 201.
    }

    // Método para actualizar un horario existente, mapeado a PUT con un parámetro de ruta.
    @PutMapping("/{id}")
    public ResponseEntity<Horario> updateHorario(@PathVariable Long id, @RequestBody Horario newHorarioData) {
        Horario updatedHorario = horarioService.updateHorario(id, newHorarioData);
        return updatedHorario != null ?
                ResponseEntity.ok().body(updatedHorario) :  // Retorna el horario actualizado con 200 OK.
                ResponseEntity.notFound().build();  // Si no se encuentra el horario, retorna 404 Not Found.
    }

    // Método para eliminar un horario por su ID, mapeado a DELETE.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHorario(@PathVariable Long id) {
        horarioService.deleteHorario(id);
        return ResponseEntity.noContent().build();  // Devuelve 204 No Content si el horario fue eliminado exitosamente.
    }
}
