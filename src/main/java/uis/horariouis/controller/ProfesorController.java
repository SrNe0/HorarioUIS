package uis.horariouis.controller;

// Importaciones de Spring Framework para manejar la inyección de dependencias, respuestas HTTP y anotaciones de mapeo en el controlador.
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
// Importación de la clase de modelo 'Profesor' y del servicio relacionado con la gestión de profesores.
import org.springframework.web.multipart.MultipartFile;
import uis.horariouis.model.Profesor;
import uis.horariouis.service.CsvServiceProfesor;
import uis.horariouis.service.ProfesorService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

// Anotación que define esta clase como un controlador REST y mapea las solicitudes a la ruta base para profesores.
@RestController
@RequestMapping("/apip/profesores")
@Tag(name = "Profesores", description = "API para la gestión de profesores")

public class ProfesorController {

    // Inyección automática del servicio 'ProfesorService' por Spring.
    private final ProfesorService profesorService;


    @Autowired
    public ProfesorController(ProfesorService profesorService) {
        this.profesorService = profesorService;

    }
    @Autowired
    private CsvServiceProfesor csvServiceProfesor;

    @Operation(summary = "Obtener todos los profesores", description = "Devuelve una lista de todos los profesores en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de profesores devuelta exitosamente",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class))})
    @GetMapping
    public ResponseEntity<List<Profesor>> getAllProfesores() {
        List<Profesor> profesores = profesorService.getAllProfesores();
        return new ResponseEntity<>(profesores, HttpStatus.OK);
    }

    @Operation(summary = "Obtener un profesor por su ID", description = "Devuelve un profesor específico según su ID.")
    @ApiResponse(responseCode = "200", description = "Profesor encontrado",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Profesor.class))})
    @ApiResponse(responseCode = "404", description = "Profesor no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<Profesor> getProfesorById(@PathVariable("id") Long id) {
        return profesorService.getProfesorById(id)
                .map(profesor -> new ResponseEntity<>(profesor, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Crear un nuevo profesor", description = "Crea un nuevo profesor en el sistema. " +
            "El usuario asociado se genera automáticamente utilizando el segundo apellido y " +
            "el primer nombre como nombre de usuario, y el documento de identidad como contraseña. " +
            "El rol del usuario se establece como 'User' (idRol=2).")
    @ApiResponse(responseCode = "201", description = "Profesor creado exitosamente",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Profesor.class))})
    @PostMapping
    public ResponseEntity<Profesor> createProfesor(@RequestBody Profesor profesor) {
        Profesor createdProfesor = profesorService.createProfesor(profesor);
        return new ResponseEntity<>(createdProfesor, HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un profesor existente", description = "Actualiza un profesor existente en el sistema. " +
            "El usuario asociado se genera automáticamente utilizando el segundo apellido y " +
            "el primer nombre como nombre de usuario, y el documento de identidad como contraseña. " +
            "El rol del usuario se establece como 'User' (idRol=2).")
    @ApiResponse(responseCode = "200", description = "Profesor actualizado exitosamente",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Profesor.class))})
    @PutMapping("/{id}")
    public ResponseEntity<Profesor> updateProfesor(@PathVariable("id") Long id, @RequestBody Profesor profesorDetails) {
        Profesor updatedProfesor = profesorService.updateProfesor(id, profesorDetails);
        return new ResponseEntity<>(updatedProfesor, HttpStatus.OK);
    }

    @Operation(summary = "Eliminar un profesor por su ID", description = "Elimina un profesor específico según su ID.")
    @ApiResponse(responseCode = "204", description = "Profesor eliminado exitosamente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfesor(@PathVariable("id") Long id) {
        profesorService.deleteProfesor(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @Operation(summary = "Importar profesores desde un archivo CSV",
              description = "Permite la carga de un archivo CSV para importar profesores.")
    @ApiResponse(responseCode = "200", description = "Archivo importado correctamente")
    @ApiResponse(responseCode = "400", description = "Archivo está vacío")
    @ApiResponse(responseCode = "500", description = "Error al procesar el archivo")
    @PostMapping("/import-csv")
    public ResponseEntity<String> importProfesoresFromCsv(
            @RequestParam("file")
            @Parameter(description = "Archivo CSV que contiene los profesores a importar.", required = true,
                    content = @Content(mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary", description = "Archivo CSV")))
            MultipartFile file
    ) {
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

    @Operation(summary = "Exportar profesores a un archivo CSV",
            description = "Exporta profesores a un archivo CSV.")
    @ApiResponse(responseCode = "200", description = "Archivo exportado correctamente")
    @GetMapping("/export-csv")
    public void exportProfesoresToCsv(HttpServletResponse response) {
        response.setContentType("application/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"profesores.csv\"");
        csvServiceProfesor.exportProfesoresToCsv(response);
    }
}
