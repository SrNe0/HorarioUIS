// Define el paquete donde se encuentra el controlador.
package uis.horariouis.controller;

// Importaciones necesarias para la funcionalidad del controlador.
import org.springframework.beans.factory.annotation.Autowired; // Permite la inyección automática de dependencias.
import org.springframework.http.HttpStatus; // Contiene los códigos de estado HTTP para respuestas.
import org.springframework.http.ResponseEntity; // Permite manejar respuestas HTTP con cuerpo y estado.
import org.springframework.web.bind.annotation.*; // Contiene anotaciones para mapear solicitudes HTTP a métodos.
import org.springframework.web.multipart.MultipartFile; // Para manejo de archivos subidos en solicitudes HTTP.
import uis.horariouis.exception.ResourceNotFoundException; // Excepción personalizada para recursos no encontrados.
import uis.horariouis.model.Asignatura; // Modelo de datos para la entidad Asignatura.
import uis.horariouis.model.ErrorResponse; // Modelo para respuestas de error personalizadas.
import uis.horariouis.service.AsignaturaService; // Servicio que contiene la lógica de negocio para Asignaturas.
import uis.horariouis.service.CsvServiceAsignatura; // Servicio para importar/exportar datos de Asignatura en formato CSV.
import javax.servlet.http.HttpServletResponse; // Utilizado para configurar la respuesta HTTP directamente.
import javax.validation.Valid; // Asegura que los objetos recibidos en los métodos están validados.
import java.util.Collections; // Utilidades para manejar colecciones, como crear mapas inmutables.
import java.util.List; // Utilizado para manejar listas de objetos, como listas de Asignaturas.

// Anotación que define esta clase como un controlador REST, mapeado a "/api/asignaturas".
@RestController
@RequestMapping("/api/asignaturas")
public class AsignaturaController {

    // Servicios inyectados que manejan la lógica de negocio.
    @Autowired
    private AsignaturaService asignaturaService;
    @Autowired
    private CsvServiceAsignatura csvServiceAsignatura;

    // Método para obtener todas las asignaturas, retorna una lista de asignaturas.
    @GetMapping
    public List<Asignatura> getAllAsignaturas() {
        return asignaturaService.findAll();
    }

    // Método para obtener una asignatura por su ID, devuelve una entidad Asignatura si se encuentra.
    @GetMapping("/{id}")
    public ResponseEntity<Asignatura> getAsignaturaById(@PathVariable Long id) {
        Asignatura asignatura = asignaturaService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asignatura not found with id: " + id));
        return ResponseEntity.ok(asignatura);
    }

    // Método para crear una nueva asignatura, valida el objeto Asignatura recibido y lo guarda.
    @PostMapping
    public ResponseEntity<?> createAsignatura(@Valid @RequestBody Asignatura asignatura) {
        try {
            Asignatura nuevaAsignatura = asignaturaService.save(asignatura);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaAsignatura);
        } catch (IllegalArgumentException ex) {
            ErrorResponse errorResponse = new ErrorResponse("Validation error", HttpStatus.BAD_REQUEST.value());
            errorResponse.setDetails(Collections.singletonMap("message", ex.getMessage()));
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Método para actualizar una asignatura existente, identificada por su ID.
    @PutMapping("/{id}")
    public ResponseEntity<Asignatura> updateAsignatura(@PathVariable Long id, @Valid @RequestBody Asignatura asignatura) {
        Asignatura asignaturaActualizada = asignaturaService.update(id, asignatura);
        return ResponseEntity.ok(asignaturaActualizada);
    }

    // Método para eliminar una asignatura por su ID, maneja la excepción si no se encuentra la asignatura.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAsignatura(@PathVariable Long id) {
        try {
            asignaturaService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException ex) {
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    // Método para importar asignaturas desde un archivo CSV, válida que el archivo no esté vacío.
    @PostMapping("/import-csv")
    public ResponseEntity<String> importEdificiosFromCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El archivo está vacío");
        }
        try {
            csvServiceAsignatura.importAsignaturasFromCsv(file);
            return ResponseEntity.ok("Archivo cargado con éxito");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar el archivo: " + e.getMessage());
        }
    }

    // Método para exportar asignaturas a un archivo CSV, configura la respuesta para descargar un archivo.
    @GetMapping("/export-csv")
    public void exportEdificiosToCsv(HttpServletResponse response) {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=edificios.csv");
        csvServiceAsignatura.exportAsignaturasToCsv(response);
    }
}
