package uis.horariouis.controller;

// Importaciones de Spring Framework para manejar la inyección de dependencias, respuestas HTTP y anotaciones de mapeo en el controlador.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// Importación de la clase de modelo 'Profesor' y del servicio relacionado con la gestión de profesores.
import uis.horariouis.model.Profesor;
import uis.horariouis.service.ProfesorService;

import java.util.List;

// Anotación que define esta clase como un controlador REST y mapea las solicitudes a la ruta base para profesores.
@RestController
@RequestMapping("/profesores")
public class ProfesorController {

    // Inyección automática del servicio 'ProfesorService' por Spring.
    private final ProfesorService profesorService;

    @Autowired
    public ProfesorController(ProfesorService profesorService) {
        this.profesorService = profesorService;
    }

    // Método para obtener todos los profesores, mapeado a una petición GET.
    @GetMapping
    public ResponseEntity<List<Profesor>> getAllProfesores() {
        // Se obtienen todos los profesores y se devuelve una respuesta con la lista de profesores y el código de estado HTTP 200 OK.
        List<Profesor> profesores = profesorService.getAllProfesores();
        return new ResponseEntity<>(profesores, HttpStatus.OK);
    }

    // Método para obtener un profesor por su ID, mapeado a GET con un parámetro de ruta.
    @GetMapping("/{id}")
    public ResponseEntity<Profesor> getProfesorById(@PathVariable("id") Long id) {
        // Se intenta obtener el profesor por su ID y se maneja la excepción si no se encuentra.
        return profesorService.getProfesorById(id)
                .map(profesor -> new ResponseEntity<>(profesor, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Método para crear un nuevo profesor, mapeado a POST.
    @PostMapping
    public ResponseEntity<Profesor> createProfesor(@RequestBody Profesor profesor) {
        // Se crea el nuevo profesor y se devuelve una respuesta con el profesor creado y el código de estado HTTP 201 Created.
        Profesor createdProfesor = profesorService.createProfesor(profesor);
        return new ResponseEntity<>(createdProfesor, HttpStatus.CREATED);
    }

    // Método para actualizar un profesor existente, mapeado a PUT con un parámetro de ruta.
    @PutMapping("/{id}")
    public ResponseEntity<Profesor> updateProfesor(@PathVariable("id") Long id, @RequestBody Profesor profesorDetails) {
        // Se intenta actualizar el profesor por su ID y se devuelve una respuesta con el profesor actualizado y el código de estado HTTP 200 OK.
        Profesor updatedProfesor = profesorService.updateProfesor(id, profesorDetails);
        return new ResponseEntity<>(updatedProfesor, HttpStatus.OK);
    }

    // Método para eliminar un profesor por su ID, mapeado a DELETE.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfesor(@PathVariable("id") Long id) {
        // Se elimina el profesor por su ID y se devuelve una respuesta sin contenido con el código de estado HTTP 204 No Content.
        profesorService.deleteProfesor(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
