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
import uis.horariouis.exception.ResourceNotFoundException;
import uis.horariouis.model.ErrorResponse;
import uis.horariouis.model.HorarioProfesor;
import uis.horariouis.service.HorarioProfesorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/horarios-profesores")
@Tag(name = "HorariosProfesores", description = "API para la gestión de horarios de profesores,recibe una hora y un profesor (para quitar" +
        "relacion muchos a muchos)")
public class HorarioProfesorController {

    @Autowired
    private HorarioProfesorService horarioProfesorService;

    @Operation(summary = "Obtener todos los horarios de profesores", description = "Obtiene una lista de todos los horarios de profesores disponibles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de horarios de profesores obtenida correctamente",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HorarioProfesor.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public List<HorarioProfesor> getAllHorariosProfesores() {
        return horarioProfesorService.getAllHorariosProfesores();
    }

    @Operation(summary = "Obtener un horario de profesor por su ID", description = "Obtiene un horario de profesor específico por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horario de profesor encontrado",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HorarioProfesor.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Horario de profesor no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<HorarioProfesor> getHorarioProfesorById(@Parameter(description = "ID del horario de profesor a buscar", required = true) @PathVariable Long id) {
        HorarioProfesor horarioProfesor = horarioProfesorService.getHorarioProfesorById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario profesor not found with id: " + id));
        return ResponseEntity.ok(horarioProfesor);
    }

    @Operation(summary = "Crear un nuevo horario de profesor", description = "Crea un nuevo horario de profesor con los datos proporcionados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Horario de profesor creado exitosamente",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HorarioProfesor.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<HorarioProfesor> createHorarioProfesor(@Valid @RequestBody HorarioProfesor horarioProfesor) {
        HorarioProfesor nuevoHorarioProfesor = horarioProfesorService.saveHorarioProfesor(horarioProfesor);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoHorarioProfesor);
    }

    @Operation(summary = "Actualizar un horario de profesor existente", description = "Actualiza un horario de profesor existente con los datos proporcionados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horario de profesor actualizado exitosamente",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HorarioProfesor.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Horario de profesor no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<HorarioProfesor> updateHorarioProfesor(@Parameter(description = "ID del horario de profesor a actualizar", required = true) @PathVariable Long id,
                                                                 @Valid @RequestBody HorarioProfesor horarioProfesor) {
        HorarioProfesor horarioProfesorActualizado = horarioProfesorService.getHorarioProfesorById(id)
                .map(horario -> {
                    horario.setProfesor(horarioProfesor.getProfesor());
                    horario.setDisponibilidadHoraria(horarioProfesor.getDisponibilidadHoraria());
                    return horarioProfesorService.saveHorarioProfesor(horario);
                }).orElseThrow(() -> new ResourceNotFoundException("Horario profesor not found with id: " + id));

        return ResponseEntity.ok(horarioProfesorActualizado);
    }

    @Operation(summary = "Eliminar un horario de profesor por su ID", description = "Elimina un horario de profesor existente por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Horario de profesor eliminado exitosamente"),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Horario de profesor no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHorarioProfesor(@Parameter(description = "ID del horario de profesor a eliminar", required = true) @PathVariable Long id) {
        horarioProfesorService.deleteHorarioProfesorById(id);
        return ResponseEntity.noContent().build();
    }
}
