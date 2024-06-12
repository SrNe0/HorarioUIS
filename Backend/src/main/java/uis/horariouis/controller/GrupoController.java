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
import uis.horariouis.dto.GrupoDTO;
import uis.horariouis.exception.ResourceNotFoundException;
import uis.horariouis.model.ErrorResponse;
import uis.horariouis.model.Grupo;
import uis.horariouis.service.CsvServiceGrupo;
import uis.horariouis.service.GrupoService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/grupos")
@Tag(name = "Grupos", description = "API para la gestión de grupos")
public class GrupoController {

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private CsvServiceGrupo csvServiceGrupo;

    @Operation(summary = "Obtener todos los grupos", description = "Obtiene una lista de todos los grupos disponibles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de grupos obtenida correctamente",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Grupo.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping()
    public List<Grupo> getAllGrupos() {
        return grupoService.getAllGrupos();
    }

    @Operation(summary = "Obtener un grupo por su ID", description = "Obtiene un grupo específico por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grupo encontrado",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Grupo.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Grupo no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public Optional<Grupo> getGrupoById(@Parameter(description = "ID del grupo a buscar", required = true) @PathVariable Long id) {
        return grupoService.getGrupoById(id);
    }

    @Operation(summary = "Eliminar un grupo por su ID", description = "Elimina un grupo existente por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Grupo eliminado exitosamente"),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Grupo no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public void deleteGrupo(@Parameter(description = "ID del grupo a eliminar", required = true) @PathVariable Long id) {
        grupoService.deleteGrupo(id);
    }

    @Operation(summary = "Crear un nuevo grupo", description = "Crea un nuevo grupo a partir de los datos proporcionados en el DTO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Grupo creado exitosamente",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Grupo.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "La asignatura no fue encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/")
    public ResponseEntity<Grupo> createGrupo(@Parameter(description = "Datos del nuevo grupo", required = true) @RequestBody GrupoDTO grupoDTO) {
        Grupo grupo = grupoService.createGrupoFromDTO(grupoDTO);
        if (grupo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(grupo);
    }

    @Operation(summary = "Actualizar un grupo existente", description = "Actualiza un grupo existente por su ID utilizando los datos proporcionados en el DTO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grupo actualizado exitosamente",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Grupo.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "El grupo o la asignatura no fueron encontrados",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Grupo> updateGrupo(@Parameter(description = "ID del grupo a actualizar", required = true) @PathVariable Long id,
                                             @Parameter(description = "Datos actualizados del grupo", required = true) @RequestBody GrupoDTO grupoDTO) {
        try {
            Grupo updatedGrupo = grupoService.updateGrupo(id, grupoDTO);
            return ResponseEntity.ok(updatedGrupo);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Importar grupos desde archivo CSV")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivo importado correctamente"),
            @ApiResponse(responseCode = "400", description = "El archivo está vacío"),
            @ApiResponse(responseCode = "500", description = "Error al procesar el archivo")
    })
    @PostMapping("/import-csv")
    public ResponseEntity<String> importGruposFromCsv(
            @RequestParam("file")
            @Parameter(description = "Archivo CSV que contiene los grupos a importar.", required = true,
                    content = @Content(mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary", description = "Archivo CSV")))
            MultipartFile file
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El archivo está vacío");
        }
        try {
            csvServiceGrupo.importGruposFromCsv(file);
            return ResponseEntity.ok("Archivo cargado con éxito");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar el archivo: " + e.getMessage());
        }
    }

    @Operation(summary = "Exportar grupos a un archivo CSV", description = "Exporta grupos a un archivo CSV.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivo exportado correctamente")
    })
    @GetMapping("/export-csv")
    public void exportGruposToCsv(HttpServletResponse response) {
        response.setContentType("application/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"grupos.csv\"");
        csvServiceGrupo.exportGruposToCsv(response);
    }
}
