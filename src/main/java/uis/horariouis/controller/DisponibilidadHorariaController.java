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
// Importación del modelo de datos 'DisponibilidadHoraria' que se utiliza en este controlador.
import uis.horariouis.model.DisponibilidadHoraria;
// Importación del servicio que encapsula la lógica de negocio para las operaciones relacionadas con 'DisponibilidadHoraria'.
import uis.horariouis.service.DisponibilidadHorariaService;
// Importación para validar objetos en las solicitudes HTTP antes de procesarlos.
import javax.validation.Valid;
import java.util.List;

// Anotación que define esta clase como un controlador REST y mapea las solicitudes a la ruta base para disponibilidad horaria.
@RestController
@RequestMapping("/api/disponibilidad-horaria")
public class DisponibilidadHorariaController {

    // Inyección automática del servicio de 'DisponibilidadHoraria' por Spring para desacoplar la creación y gestión de dependencias.
    @Autowired
    private DisponibilidadHorariaService disponibilidadHorariaService;

    // Método que maneja las solicitudes GET para obtener todas las disponibilidades horarias disponibles.
    // Retorna una lista de objetos 'DisponibilidadHoraria'.
    @GetMapping
    public List<DisponibilidadHoraria> getAllDisponibilidadHoraria() {
        return disponibilidadHorariaService.getAllDisponibilidadesHorarias();
    }

    // Método que maneja las solicitudes GET para obtener una disponibilidad horaria específica por su ID.
    // Utiliza 'PathVariable' para capturar el ID desde la URL.
    @GetMapping("/{id}")
    public ResponseEntity<DisponibilidadHoraria> getDisponibilidadHorariaById(@PathVariable Long id) {
        DisponibilidadHoraria disponibilidadHoraria = disponibilidadHorariaService.getDisponibilidadHorariaById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidad horaria not found with id: " + id));
        return ResponseEntity.ok(disponibilidadHoraria);  // Si se encuentra, retorna 200 OK con la disponibilidad horaria.
    }

    // Método que maneja las solicitudes POST para crear una nueva disponibilidad horaria.
    // 'RequestBody' captura el objeto 'DisponibilidadHoraria' enviado en la solicitud, y 'Valid' asegura que el objeto cumpla con las validaciones definidas.
    @PostMapping
    public ResponseEntity<?> createDisponibilidadHoraria(@Valid @RequestBody DisponibilidadHoraria disponibilidadHoraria) {
        DisponibilidadHoraria nuevaDisponibilidadHoraria = disponibilidadHorariaService.saveDisponibilidadHoraria(disponibilidadHoraria);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaDisponibilidadHoraria);  // Retorna la disponibilidad horaria creada con estado HTTP 201.
    }

    // Método que maneja las solicitudes PUT para actualizar una disponibilidad horaria existente por su ID.
    // Combina 'PathVariable' y 'RequestBody' para obtener el ID y los nuevos datos respectivamente.
    @PutMapping("/{id}")
    public ResponseEntity<DisponibilidadHoraria> updateDisponibilidadHoraria(@PathVariable Long id, @Valid @RequestBody DisponibilidadHoraria disponibilidadHoraria) {
        DisponibilidadHoraria disponibilidadHorariaActualizada = disponibilidadHorariaService.getDisponibilidadHorariaById(id)
                .map(disponibilidad -> {
                    disponibilidad.setDia(disponibilidadHoraria.getDia());
                    disponibilidad.setHoraInicio(disponibilidadHoraria.getHoraInicio());
                    disponibilidad.setHoraFin(disponibilidadHoraria.getHoraFin());
                    return disponibilidadHorariaService.saveDisponibilidadHoraria(disponibilidad);
                }).orElseThrow(() -> new ResourceNotFoundException("Disponibilidad horaria not found with id: " + id));

        return ResponseEntity.ok(disponibilidadHorariaActualizada);  // Retorna la disponibilidad horaria actualizada con 200 OK.
    }

    // Método que maneja las solicitudes DELETE para eliminar una disponibilidad horaria por su ID.
    // Utiliza 'PathVariable' para capturar el ID desde la URL.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDisponibilidadHoraria(@PathVariable Long id) {
        disponibilidadHorariaService.deleteDisponibilidadHorariaById(id);
        return ResponseEntity.noContent().build();  // Devuelve 204 No Content si la disponibilidad horaria fue eliminada exitosamente.
    }
}
