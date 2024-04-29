package uis.horariouis.controller;

// Importaciones de Spring para manejo de inyección de dependencias y respuestas HTTP.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// Importaciones para manejar las solicitudes HTTP y mapeo de rutas.
import org.springframework.web.bind.annotation.*;
// Importaciones de los modelos de datos y transferencia (DTO) usados en el controlador.
import uis.horariouis.dto.AulaDTO;
import uis.horariouis.model.Aula;
// Importación del servicio que contiene la lógica de negocio para la entidad Aula.
import uis.horariouis.service.AulaService;

import java.util.List;

// Anotación que define la clase como un controlador REST y mapea las solicitudes a la ruta base para aulas.
@RestController
@RequestMapping("/api/aulas")
public class AulaController {

    // Inyección del servicio AulaService, que proporciona los métodos para operar sobre la entidad Aula.
    @Autowired
    private AulaService aulaService;

    // Método para crear un aula, mapeado a POST en la ruta específica "/create".
    // Recibe un AulaDTO y crea una instancia de Aula.
    @PostMapping("/create")
    public ResponseEntity<Aula> createAula(@RequestBody AulaDTO aulaDTO) {
        Aula aulaCreada = aulaService.createOrUpdateAula(aulaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(aulaCreada);  // Retorna el aula creada con estado HTTP 201.
    }

    // Método para obtener todas las aulas, mapeado a GET en la ruta base.
    // Retorna una lista de todas las aulas disponibles.
    @GetMapping
    public List<Aula> getAllAulas() {
        return aulaService.getAllAulas();
    }

    // Método para obtener una aula específica por su ID, mapeado a GET con un parámetro de ruta.
    // Utiliza el servicio para encontrar el aula y retorna una respuesta según si se encuentra o no.
    @GetMapping("/{id}")
    public ResponseEntity<Aula> getAulaById(@PathVariable Long id) {
        return aulaService.getAulaById(id)
                .map(ResponseEntity::ok)  // Si se encuentra el aula, retorna 200 OK con el aula.
                .orElse(ResponseEntity.notFound().build());  // Si no se encuentra, retorna 404 Not Found.
    }

    // Método para actualizar una aula, mapeado a PUT en la ruta con el ID de aula.
    // Recibe un AulaDTO como datos nuevos para actualizar el aula existente.
    @PutMapping("/{id}")
    public ResponseEntity<Aula> updateAula(@PathVariable Long id, @RequestBody AulaDTO aulaDTO) {
        return aulaService.updateAula(id, aulaDTO)
                .map(aulaActualizada -> ResponseEntity.ok().body(aulaActualizada))  // Retorna el aula actualizada con 200 OK.
                .orElse(ResponseEntity.notFound().build());  // Si el aula no existe, retorna 404 Not Found.
    }

    // Método para eliminar una aula por su ID, mapeado a DELETE en la ruta con el ID de aula.
    // Verifica si el aula fue eliminada y retorna una respuesta acorde.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAula(@PathVariable Long id) {
        boolean deleted = aulaService.deleteAula(id);
        if (deleted) {
            return ResponseEntity.ok().build();  // Devuelve 200 OK si el aula fue eliminada exitosamente.
        } else {
            return ResponseEntity.notFound().build();  // Devuelve 404 Not Found si el aula no se encuentra para eliminar.
        }
    }
}
