package uis.horariouis.controller;

// Importaciones de Spring Framework para manejar la inyección de dependencias.
import org.springframework.beans.factory.annotation.Autowired;
// Importaciones de Spring para manejar respuestas HTTP y códigos de estado.
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// Importaciones de Spring para manejar solicitudes HTTP y anotaciones de mapeo en el controlador.
import org.springframework.web.bind.annotation.*;
// Importación de una excepción personalizada para manejar casos donde el recurso solicitado no se encuentra.
import uis.horariouis.exception.ResourceNotFoundException;
// Importación del modelo de datos 'Accion' que se utiliza en este controlador.
import uis.horariouis.model.Accion;
// Importación del servicio que encapsula la lógica de negocio para las operaciones relacionadas con 'Accion'.
import uis.horariouis.service.AccionService;
// Importación para validar objetos en las solicitudes HTTP antes de procesarlos.
import javax.validation.Valid;
import java.util.List;

// Anotación que define esta clase como un controlador REST, indicando que es parte de la capa web que escucha en la ruta '/api/acciones'.
@RestController
@RequestMapping("/api/acciones")
public class AccionController {

    // Inyección automática del servicio de 'Accion' por Spring para desacoplar la creación y gestión de dependencias.
    @Autowired
    private AccionService accionService;

    // Método que maneja las solicitudes GET para obtener todas las acciones disponibles.
    // Retorna una lista de objetos 'Accion'.
    @GetMapping
    public List<Accion> getAllAcciones() {
        return accionService.getAllAcciones();
    }

    // Método que maneja las solicitudes GET para obtener una acción específica por su ID.
    // Utiliza 'PathVariable' para capturar el ID de la acción desde la URL.
    @GetMapping("/{id}")
    public ResponseEntity<Accion> getAccionById(@PathVariable Long id) {
        Accion accion = accionService.getAccionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Accion not found with id: " + id));
        return ResponseEntity.ok(accion);
    }

    // Método que maneja las solicitudes POST para crear una nueva acción.
    // 'RequestBody' captura el objeto 'Accion' enviado en la solicitud, y 'Valid' asegura que el objeto cumpla con las validaciones definidas.
    @PostMapping
    public ResponseEntity<?> createAccion(@Valid @RequestBody Accion accion) {
        Accion nuevaAccion = accionService.saveAccion(accion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaAccion);
    }

    // Método que maneja las solicitudes PUT para actualizar una acción existente por su ID.
    // Combina 'PathVariable' y 'RequestBody' para obtener el ID de la acción y los nuevos datos respectivamente.
    @PutMapping("/{id}")
    public ResponseEntity<Accion> updateAccion(@PathVariable Long id, @Valid @RequestBody Accion accion) {
        Accion accionActualizada = accionService.saveAccion(accion);
        return ResponseEntity.ok(accionActualizada);
    }

    // Método que maneja las solicitudes DELETE para eliminar una acción por su ID.
    // Utiliza 'PathVariable' para capturar el ID de la acción desde la URL.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccion(@PathVariable Long id) {
        accionService.deleteAccionById(id);
        return ResponseEntity.noContent().build();
    }
}
