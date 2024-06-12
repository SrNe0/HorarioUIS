package uis.horariouis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uis.horariouis.exception.ResourceNotFoundException;
import uis.horariouis.model.Asignatura;
import uis.horariouis.model.ErrorResponse;
import uis.horariouis.service.AsignaturaService;
import uis.horariouis.service.CsvServiceAsignatura;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/asignaturas")
@Tag(name = "Asignaturas", description = "API para la gestión de asignaturas")
public class AsignaturaController {

    @Autowired
    private AsignaturaService asignaturaService;
    @Autowired
    private CsvServiceAsignatura csvServiceAsignatura;

    @Operation(summary = "Obtener todas las asignaturas", description = "Devuelve una lista de todas las asignaturas disponibles.")
    @ApiResponse(responseCode = "200", description = "Lista de asignaturas obtenida correctamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Asignatura.class)))
    @GetMapping
    public List<Asignatura> getAllAsignaturas() {
        return asignaturaService.findAll();
    }

    @Operation(summary = "Obtener una asignatura por ID", description = "Devuelve una asignatura específica por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asignatura encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Asignatura.class))),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Asignatura no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Asignatura> getAsignaturaById(@Parameter(description = "ID de la asignatura a buscar", required = true) @PathVariable Long id) {
        Asignatura asignatura = asignaturaService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asignatura not found with id: " + id));
        return ResponseEntity.ok(asignatura);
    }

    @Operation(summary = "Crear una nueva asignatura", description = "Crea una nueva asignatura con los datos proporcionados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Asignatura creada correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Asignatura.class))),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/")
    public ResponseEntity<Asignatura> createAsignatura(@Valid @RequestBody Asignatura asignatura) {
        Asignatura nuevaAsignatura = asignaturaService.save(asignatura);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaAsignatura);
    }

    @Operation(summary = "Actualizar una asignatura existente", description = "Actualiza los datos de una asignatura existente basada en su ID, se tiene que agregar todos los datos " +
            "incluidos los que no se van a editar.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asignatura actualizada correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Asignatura.class))),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Asignatura no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Asignatura> updateAsignatura(@PathVariable Long id, @Valid @RequestBody Asignatura asignatura) {
        Asignatura asignaturaActualizada = asignaturaService.update(id, asignatura);
        return ResponseEntity.ok(asignaturaActualizada);
    }

    @Operation(summary = "Eliminar una asignatura por ID", description = "Elimina una asignatura basada en su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Asignatura eliminada correctamente"),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Asignatura no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsignatura(@Parameter(description = "ID de la asignatura a eliminar", required = true) @PathVariable Long id) {
        try {
            asignaturaService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Importar asignaturas desde un archivo CSV", description = "Permite la carga de un archivo CSV para importar asignaturas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivo importado correctamente"),
            @ApiResponse(responseCode = "400", description = "El archivo está vacío"),
            @ApiResponse(responseCode = "500", description = "Error al procesar el archivo")
    })
    @PostMapping("/import-csv")
    public ResponseEntity<String> importAsignaturasFromCsv(
            @RequestParam("file")
            @Parameter(description = "Archivo CSV que contiene las asignaturas a importar.", required = true,
                    content = @Content(mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary", description = "Archivo CSV")))
            MultipartFile file
    ) {
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

    @Operation(summary = "Exportar asignaturas a un archivo CSV", description = "Exporta asignaturas a un archivo CSV.")
    @ApiResponse(responseCode = "200", description = "Archivo exportado correctamente")
    @GetMapping("/export-csv")
    public void exportAsignaturasToCsv(HttpServletResponse response) {
        response.setContentType("application/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"asignaturas.csv\"");
        csvServiceAsignatura.exportAsignaturasToCsv(response);
    }
}
