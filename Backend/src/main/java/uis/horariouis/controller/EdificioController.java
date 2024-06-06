package uis.horariouis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uis.horariouis.exception.ResourceNotFoundException;
import uis.horariouis.model.Edificio;
import uis.horariouis.service.CsvServiceEdificio;
import uis.horariouis.service.EdificioService;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/edificios")
@Tag(name = "Edificios", description = "API para la gestión de edificios")
public class EdificioController {

    @Autowired
    private EdificioService edificioService;
    @Autowired
    private CsvServiceEdificio csvServiceEdificio;

    @Operation(summary = "Obtener todos los edificios",
            description = "Obtiene una lista de todos los edificios disponibles.")
    @GetMapping("")
    public List<Edificio> getAllEdificios() {
        return edificioService.getAllEdificios();
    }

    @Operation(summary = "Obtener un edificio por su ID",
            description = "Obtiene un edificio específico por su ID.")
    @ApiResponse(responseCode = "200", description = "Edificio encontrado",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Edificio.class))})
    @ApiResponse(responseCode = "404", description = "Edificio no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<Edificio> getEdificioById(@PathVariable Long id) {
        return edificioService.getEdificioById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear un edificio",
            description = "Crea un nuevo edificio a partir de los datos proporcionados.")
    @ApiResponse(responseCode = "201", description = "Edificio creado exitosamente",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Edificio.class))})
    @PostMapping("/")
    public ResponseEntity<Edificio> createEdificio(@RequestBody Edificio edificio) {
        Edificio newEdificio = edificioService.createEdificio(edificio);
        return ResponseEntity.status(HttpStatus.CREATED).body(newEdificio);
    }

    @Operation(summary = "Actualizar un edificio existente",
            description = "Actualiza un edificio existente por su ID utilizando los datos proporcionados.")
    @ApiResponse(responseCode = "200", description = "Edificio actualizado exitosamente",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Edificio.class))})
    @ApiResponse(responseCode = "404", description = "Edificio no encontrado")
    @PutMapping("/{id}")
    public ResponseEntity<Edificio> updateEdificio(@PathVariable Long id, @RequestBody Edificio edificio) {
        try {
            Edificio updatedEdificio = edificioService.updateEdificio(id, edificio);
            return ResponseEntity.ok(updatedEdificio);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar un edificio por su ID",
            description = "Elimina un edificio existente por su ID.")
    @ApiResponse(responseCode = "204", description = "Edificio eliminado exitosamente")
    @DeleteMapping("/{id}")
    public void deleteEdificio(@PathVariable Long id) {
        edificioService.deleteEdificio(id);
    }

    @Operation(summary = "Importar edificios desde un archivo CSV")
    @PostMapping("/import-csv")
    public ResponseEntity<String> importEdificiosFromCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El archivo está vacío");
        }
        try {
            csvServiceEdificio.importEdificiosFromCsv(file);
            return ResponseEntity.ok("Archivo cargado con éxito");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar el archivo: " + e.getMessage());
        }
    }

    @Operation(summary = "Exportar edificios a un archivo CSV",
            description = "Exporta edificios a un archivo CSV.")
    @GetMapping("/export-csv")
    public void exportEdificiosToCsv(HttpServletResponse response) {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=edificios.csv");
        csvServiceEdificio.exportEdificiosToCsv(response);
    }
}
