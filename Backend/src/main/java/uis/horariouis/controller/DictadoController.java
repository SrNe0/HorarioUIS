package uis.horariouis.controller;

// Importaciones de Spring para manejar la inyección de dependencias.
import org.springframework.beans.factory.annotation.Autowired;
// Importaciones de Spring para manejar respuestas HTTP y códigos de estado.
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// Importaciones para manejar las solicitudes HTTP y anotaciones de mapeo en el controlador.
import org.springframework.web.bind.annotation.*;
// Importación de una excepción personalizada para manejar casos donde el recurso solicitado no se encuentra.
import uis.horariouis.exception.ResourceNotFoundException;
// Importación del modelo de datos 'Dictado' que se utiliza en este controlador.
import uis.horariouis.model.Dictado;
// Importación del servicio que encapsula la lógica de negocio para las operaciones relacionadas con 'Dictado'.
import uis.horariouis.service.DictadoService;
// Importación para validar objetos en las solicitudes HTTP antes de procesarlos.
import javax.validation.Valid;
import java.util.List;

// Anotación que define esta clase como un controlador REST y mapea las solicitudes a la ruta base para dictados.
@RestController
@RequestMapping("/api/dictados")
public class DictadoController {

    // Inyección automática del servicio de 'Dictado' por Spring para desacoplar la creación y gestión de dependencias.
    @Autowired
    private DictadoService dictadoService;

    // Método que maneja las solicitudes GET para obtener todos los dictados disponibles.
    // Retorna una lista de objetos 'Dictado'.
    @GetMapping
    public List<Dictado> getAllDictados() {
        return dictadoService.getAllDictados();
    }

    // Método que maneja las solicitudes GET para obtener un dictado específico por su ID.
    // Utiliza 'PathVariable' para capturar el ID del dictado desde la URL.
    @GetMapping("/{id}")
    public ResponseEntity<Dictado> getDictadoById(@PathVariable Long id) {
        Dictado dictado = dictadoService.getDictadoById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dictado not found with id: " + id));
        return ResponseEntity.ok(dictado);  // Si se encuentra el dictado, retorna 200 OK con el dictado.
    }

    // Método que maneja las solicitudes POST para crear un nuevo dictado.
    // 'RequestBody' captura el objeto 'Dictado' enviado en la solicitud, y 'Valid' asegura que el objeto cumpla con las validaciones definidas.
    @PostMapping
    public ResponseEntity<?> createDictado(@Valid @RequestBody Dictado dictado) {
        Dictado nuevoDictado = dictadoService.saveDictado(dictado);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDictado);  // Retorna el dictado creado con estado HTTP 201.
    }

    // Método que maneja las solicitudes PUT para actualizar un dictado existente por su ID.
    // Combina 'PathVariable' y 'RequestBody' para obtener el ID del dictado y los nuevos datos respectivamente.
    @PutMapping("/{id}")
    public ResponseEntity<Dictado> updateDictado(@PathVariable Long id, @Valid @RequestBody Dictado dictado) {
        Dictado dictadoActualizado = dictadoService.saveDictado(dictado);
        return ResponseEntity.ok(dictadoActualizado);  // Retorna el dictado actualizado con 200 OK.
    }

    // Método que maneja las solicitudes DELETE para eliminar un dictado por su ID.
    // Utiliza 'PathVariable' para capturar el ID del dictado desde la URL.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDictado(@PathVariable Long id) {
        dictadoService.deleteDictadoById(id);
        return ResponseEntity.noContent().build();  // Devuelve 204 No Content si el dictado fue eliminado exitosamente.
    }
}
