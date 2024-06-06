package uis.horariouis.controller;

// Importaciones de Spring Framework para manejar la inyección de dependencias, respuestas HTTP y anotaciones de mapeo en el controlador.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// Importación de la excepción de manejo de recursos no encontrados y del modelo 'RolAccion'.
import uis.horariouis.exception.ResourceNotFoundException;
import uis.horariouis.model.RolAccion;
// Importación del servicio relacionado con la gestión de roles y acciones.
import uis.horariouis.service.RolAccionService;

import javax.validation.Valid;
import java.util.List;

// Anotación que define esta clase como un controlador REST y mapea las solicitudes a la ruta base para roles y acciones.
@RestController
@RequestMapping("/api/rol-acciones")
public class RolAccionController {

    // Inyección automática del servicio 'RolAccionService' por Spring.
    @Autowired
    private RolAccionService rolAccionService;

    // Método para obtener todas las relaciones de rol y acción, mapeado a una petición GET.
    @GetMapping
    public List<RolAccion> getAllRolAcciones() {
        // Se obtienen todas las relaciones de rol y acción y se devuelven como respuesta.
        return rolAccionService.getAllRolAcciones();
    }

    // Método para obtener una relación de rol y acción por su ID, mapeado a GET con un parámetro de ruta.
    @GetMapping("/{id}")
    public ResponseEntity<RolAccion> getRolAccionById(@PathVariable Long id) {
        // Se intenta obtener la relación de rol y acción por su ID y se maneja la excepción si no se encuentra.
        RolAccion rolAccion = rolAccionService.getRolAccionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol-acción not found with id: " + id));
        return ResponseEntity.ok(rolAccion);
    }

    // Método para crear una nueva relación de rol y acción, mapeado a POST.
    @PostMapping
    public ResponseEntity<?> createRolAccion(@Valid @RequestBody RolAccion rolAccion) {
        // Se crea la nueva relación de rol y acción y se devuelve una respuesta con la relación creada y el código de estado HTTP 201 Created.
        RolAccion nuevoRolAccion = rolAccionService.saveRolAccion(rolAccion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoRolAccion);
    }

    // Método para actualizar una relación de rol y acción existente, mapeado a PUT con un parámetro de ruta.
    @PutMapping("/{id}")
    public ResponseEntity<RolAccion> updateRolAccion(@PathVariable Long id, @Valid @RequestBody RolAccion rolAccion) {
        // Se intenta actualizar la relación de rol y acción por su ID y se maneja la excepción si no se encuentra.
        RolAccion rolAccionActualizado = rolAccionService.getRolAccionById(id)
                .map(rol -> {
                    // Se actualizan los campos de la relación de rol y acción con los valores proporcionados.
                    rol.setRol(rolAccion.getRol());
                    rol.setAccion(rolAccion.getAccion());
                    // Si hay más campos, actualízalos aquí
                    return rolAccionService.saveRolAccion(rol);
                }).orElseThrow(() -> new ResourceNotFoundException("Rol-acción not found with id: " + id));

        return ResponseEntity.ok(rolAccionActualizado);
    }

    // Método para eliminar una relación de rol y acción por su ID, mapeado a DELETE.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRolAccion(@PathVariable Long id) {
        // Se elimina la relación de rol y acción por su ID y se devuelve una respuesta sin contenido con el código de estado HTTP 204 No Content.
        rolAccionService.deleteRolAccionById(id);
        return ResponseEntity.noContent().build();
    }
}
