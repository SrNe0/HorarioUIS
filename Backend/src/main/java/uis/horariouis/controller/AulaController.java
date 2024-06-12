package uis.horariouis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uis.horariouis.dto.AulaDTO;
import uis.horariouis.model.Aula;
import uis.horariouis.model.ErrorResponse;
import uis.horariouis.service.AulaService;
import uis.horariouis.service.CsvServiceAula;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/aulas")
@Tag(name = "Aulas", description = "API para la gestión de aulas")
public class AulaController {

    @Autowired
    private AulaService aulaService;
    @Autowired
    private CsvServiceAula csvServiceAula;

    @Operation(summary = "Crear un aula", description = "Crea un nuevo aula a partir de los datos proporcionados en el DTO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Aula creada exitosamente",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Aula.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/")
    public ResponseEntity<Aula> createAula(@RequestBody AulaDTO aulaDTO) {
        Aula aulaCreada = aulaService.createOrUpdateAula(aulaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(aulaCreada);
    }

    @Operation(summary = "Obtener todas las aulas", description = "Obtiene una lista de todas las aulas disponibles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de aulas obtenida correctamente",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Aula.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public List<Aula> getAllAulas() {
        return aulaService.getAllAulas();
    }

    @Operation(summary = "Obtener un aula por su ID", description = "Obtiene un aula específica por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aula encontrada",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Aula.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Aula no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Aula> getAulaById(@PathVariable Long id) {
        return aulaService.getAulaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar un aula existente", description = "Actualiza un aula existente por su ID utilizando los datos proporcionados en el DTO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aula actualizada exitosamente",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Aula.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Aula no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Aula> updateAula(@PathVariable Long id, @RequestBody AulaDTO aulaDTO) {
        return aulaService.updateAula(id, aulaDTO)
                .map(aulaActualizada -> ResponseEntity.ok().body(aulaActualizada))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar un aula por su ID", description = "Elimina un aula existente por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Aula eliminada exitosamente"),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Aula no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAula(@PathVariable Long id) {
        boolean deleted = aulaService.deleteAula(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Importar aulas desde archivo CSV")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivo importado correctamente"),
            @ApiResponse(responseCode = "400", description = "El archivo está vacío"),
            @ApiResponse(responseCode = "500", description = "Error al procesar el archivo")
    })
    @PostMapping("/import-csv")
    public ResponseEntity<String> importGruposFromCsv(
            @RequestParam("file")
            @Parameter(description = "Archivo CSV que contiene las aulas a importar.", required = true,
                    content = @Content(mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary", description = "Archivo CSV")))
            MultipartFile file
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El archivo está vacío");
        }
        try {
            csvServiceAula.importAulasFromCsv(file);
            return ResponseEntity.ok("Archivo cargado con éxito");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar el archivo: " + e.getMessage());
        }
    }

    @Operation(summary = "Exportar aulas a un archivo CSV", description = "Exporta aulas a un archivo CSV.")
    @ApiResponse(responseCode = "200", description = "Archivo exportado correctamente")
    @GetMapping("/export-csv")
    public void exportGruposToCsv(HttpServletResponse response) {
        csvServiceAula.exportAulasToCsv(response);
    }
}
