package uis.horariouis.controller;

// Importaciones de Spring para manejar la inyección de dependencias y respuestas HTTP.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// Importaciones para manejar las solicitudes HTTP y anotaciones de mapeo en el controlador.
import org.springframework.web.bind.annotation.*;
// Importaciones de clases de excepción personalizadas, DTO y el modelo de datos 'Grupo'.
import uis.horariouis.dto.GrupoDTO;
import uis.horariouis.exception.ResourceNotFoundException;
import uis.horariouis.model.Grupo;
// Importación del servicio relacionado con la gestión de grupos.
import uis.horariouis.service.GrupoService;
// Importaciones para manejar tipos opcionales y listas.
import java.util.List;
import java.util.Optional;

// Anotación que define esta clase como un controlador REST y mapea las solicitudes a la ruta base para grupos.
@RestController
@RequestMapping("/grupos")
public class GrupoController {

    // Inyección automática del servicio de 'GrupoService' por Spring para desacoplar la creación y gestión de dependencias.
    @Autowired
    private GrupoService grupoService;

    // Método que maneja las solicitudes GET para obtener todos los grupos disponibles.
    @GetMapping("/")
    public List<Grupo> getAllGrupos() {
        return grupoService.getAllGrupos();
    }

    // Método que maneja las solicitudes GET para obtener un grupo específico por su ID.
    @GetMapping("/{id}")
    public Optional<Grupo> getGrupoById(@PathVariable Long id) {
        return grupoService.getGrupoById(id);
    }

    // Método que maneja las solicitudes DELETE para eliminar un grupo por su ID.
    @DeleteMapping("/{id}")
    public void deleteGrupo(@PathVariable Long id) {
        grupoService.deleteGrupo(id);
    }

    // Método que maneja las solicitudes POST para crear un nuevo grupo a partir de un DTO.
    @PostMapping
    public ResponseEntity<Grupo> createGrupo(@RequestBody GrupoDTO grupoDTO) {
        Grupo grupo = grupoService.createGrupoFromDTO(grupoDTO);
        if (grupo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(grupo);  // Retorna el grupo creado con estado HTTP 201.
    }

    // Método que maneja las solicitudes PUT para actualizar un grupo existente por su ID.
    @PutMapping("/{id}")
    public ResponseEntity<Grupo> updateGrupo(@PathVariable Long id, @RequestBody GrupoDTO grupoDTO) {
        try {
            Grupo updatedGrupo = grupoService.updateGrupo(id, grupoDTO);
            return ResponseEntity.ok(updatedGrupo);  // Retorna el grupo actualizado con 200 OK.
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();  // Si no se encuentra el grupo, retorna 404 Not Found.
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);  // Si ocurre un error interno, retorna 500 Internal Server Error.
        }
    }
}
