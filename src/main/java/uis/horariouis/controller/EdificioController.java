package uis.horariouis.controller;

// Importaciones de Spring para manejar la inyección de dependencias y respuestas HTTP.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// Importaciones para manejar las solicitudes HTTP y anotaciones de mapeo en el controlador.
import org.springframework.web.bind.annotation.*;
// Importaciones de clases de excepción personalizadas y el modelo de datos 'Edificio'.
import uis.horariouis.exception.ResourceNotFoundException;
import uis.horariouis.model.Edificio;
// Importaciones de servicios relacionados con la gestión de edificios y la importación/exportación de datos desde/hacia archivos CSV.
import uis.horariouis.service.CsvServiceEdificio;
import uis.horariouis.service.EdificioService;
// Importaciones para manejar archivos MultipartFile y la respuesta HTTP de tipo HttpServletResponse.
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

// Anotación que define esta clase como un controlador REST y mapea las solicitudes a la ruta base para edificios.
@RestController
@RequestMapping("/api/edificios")
public class EdificioController {

    // Inyección automática del servicio de 'EdificioService' y 'CsvServiceEdificio' por Spring para desacoplar la creación y gestión de dependencias.
    @Autowired
    private EdificioService edificioService;
    @Autowired
    private CsvServiceEdificio csvServiceEdificio;

    // Método que maneja las solicitudes GET para obtener todos los edificios disponibles.
    @GetMapping("/")
    public List<Edificio> getAllEdificios() {
        return edificioService.getAllEdificios();
    }

    // Método que maneja las solicitudes GET para obtener un edificio específico por su ID.
    @GetMapping("/{id}")
    public ResponseEntity<Edificio> getEdificioById(@PathVariable Long id) {
        return edificioService.getEdificioById(id)
                .map(ResponseEntity::ok)  // Si se encuentra, retorna 200 OK con el edificio.
                .orElse(ResponseEntity.notFound().build());  // Si no se encuentra, retorna 404 Not Found.
    }

    // Método que maneja las solicitudes POST para crear un nuevo edificio.
    @PostMapping("/")
    public ResponseEntity<Edificio> createEdificio(@RequestBody Edificio edificio) {
        Edificio newEdificio = edificioService.createEdificio(edificio);
        return ResponseEntity.status(HttpStatus.CREATED).body(newEdificio);  // Retorna el edificio creado con estado HTTP 201.
    }

    // Método que maneja las solicitudes PUT para actualizar un edificio existente por su ID.
    @PutMapping("/{id}")
    public ResponseEntity<Edificio> updateEdificio(@PathVariable Long id, @RequestBody Edificio edificio) {
        try {
            Edificio updatedEdificio = edificioService.updateEdificio(id, edificio);
            return ResponseEntity.ok(updatedEdificio);  // Retorna el edificio actualizado con 200 OK.
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();  // Si no se encuentra el edificio, retorna 404 Not Found.
        }
    }

    // Método que maneja las solicitudes DELETE para eliminar un edificio por su ID.
    @DeleteMapping("/{id}")
    public void deleteEdificio(@PathVariable Long id) {
        edificioService.deleteEdificio(id);
    }

    // Método que maneja las solicitudes POST para importar edificios desde un archivo CSV.
    @PostMapping("/import-csv")
    public ResponseEntity<String> importEdificiosFromCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El archivo está vacío");
        }
        try {
            csvServiceEdificio.importEdificiosFromCsv(file);
            return ResponseEntity.ok("Archivo cargado con éxito");  // Retorna un mensaje de éxito si la importación fue exitosa.
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar el archivo: " + e.getMessage());
        }
    }

    // Método que maneja las solicitudes GET para exportar edificios a un archivo CSV.
    @GetMapping("/export-csv")
    public void exportEdificiosToCsv(HttpServletResponse response) {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=edificios.csv");
        csvServiceEdificio.exportEdificiosToCsv(response);
    }
}
