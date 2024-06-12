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
import uis.horariouis.model.ErrorResponse;
import uis.horariouis.model.Profesor;
import uis.horariouis.service.CsvServiceProfesor;
import uis.horariouis.service.ProfesorService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/profesores")
@Tag(name = "Profesores", description = "API para la gestión de profesores")
public class ProfesorController {

    private final ProfesorService profesorService;

    @Autowired
    public ProfesorController(ProfesorService profesorService) {
        this.profesorService = profesorService;
    }

    @Autowired
    private CsvServiceProfesor csvServiceProfesor;

    @Operation(summary = "Obtener todos los profesores", description = "Devuelve una lista de todos los profesores en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de profesores devuelta exitosamente",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<Profesor>> getAllProfesores() {
        List<Profesor> profesores = profesorService.getAllProfesores();
        return new ResponseEntity<>(profesores, HttpStatus.OK);
    }

    @Operation(summary = "Obtener un profesor por su ID", description = "Devuelve un profesor específico según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profesor encontrado",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Profesor.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Profesor no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Profesor> getProfesorById(@PathVariable("id") Long id) {
        return profesorService.getProfesorById(id)
                .map(profesor -> new ResponseEntity<>(profesor, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Crear un nuevo profesor", description = "Crea un nuevo profesor en el sistema. El usuario asociado se genera automáticamente utilizando el segundo apellido y el primer nombre como nombre de usuario, y el documento de identidad como contraseña. El rol del usuario se establece como 'User' (idRol=2).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Profesor creado exitosamente",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Profesor.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<Profesor> createProfesor(@RequestBody Profesor profesor) {
        Profesor createdProfesor = profesorService.createProfesor(profesor);
        return new ResponseEntity<>(createdProfesor, HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un profesor existente", description = "Actualiza un profesor existente en el sistema. El usuario asociado se genera automáticamente utilizando el segundo apellido y el primer nombre como nombre de usuario, y el documento de identidad como contraseña. El rol del usuario se establece como 'User' (idRol=2).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profesor actualizado exitosamente",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Profesor.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Profesor no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Profesor> updateProfesor(@PathVariable("id") Long id, @RequestBody Profesor profesorDetails) {
        Profesor updatedProfesor = profesorService.updateProfesor(id, profesorDetails);
        return new ResponseEntity<>(updatedProfesor, HttpStatus.OK);
    }

    @Operation(summary = "Eliminar un profesor por su ID", description = "Elimina un profesor específico según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Profesor eliminado exitosamente"),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Profesor no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfesor(@PathVariable("id") Long id) {
        profesorService.deleteProfesor(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Importar profesores desde un archivo CSV", description = "Permite la carga de un archivo CSV para importar profesores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivo importado correctamente"),
            @ApiResponse(responseCode = "400", description = "El archivo está vacío"),
            @ApiResponse(responseCode = "500", description = "Error al procesar el archivo")
    })
    @PostMapping("/import-csv")
    public ResponseEntity<String> importProfesoresFromCsv(
            @RequestParam("file")
            @Parameter(description = "Archivo CSV que contiene los profesores a importar.", required = true,
                    content = @Content(mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary", description = "Archivo CSV")))
            MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El archivo está vacío");
        }
        try {
            csvServiceProfesor.importProfesoresFromCsv(file);
            return ResponseEntity.ok("Archivo cargado con éxito");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar el archivo: " + e.getMessage());
        }
    }

    @Operation(summary = "Exportar profesores a un archivo CSV", description = "Exporta profesores a un archivo CSV.")
    @ApiResponse(responseCode = "200", description = "Archivo exportado correctamente")
    @GetMapping("/export-csv")
    public void exportProfesoresToCsv(HttpServletResponse response) {
        response.setContentType("application/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"profesores.csv\"");
        csvServiceProfesor.exportProfesoresToCsv(response);
    }
}
