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
import uis.horariouis.model.ErrorResponse;
import uis.horariouis.model.Horario;
import uis.horariouis.service.HorarioService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/horarios")
@Tag(name = "Horarios", description = "API para la gestión de horarios")
public class HorarioController {

    private final HorarioService horarioService;

    @Autowired
    public HorarioController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    @Operation(summary = "Obtener todos los horarios", description = "Obtiene una lista de todos los horarios disponibles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de horarios obtenida correctamente",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Horario.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public List<Horario> getAllHorarios() {
        return horarioService.getAllHorarios();
    }

    @Operation(summary = "Obtener un horario por su ID", description = "Obtiene un horario específico por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horario encontrado",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Horario.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Horario no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Horario> getHorarioById(@Parameter(description = "ID del horario a buscar", required = true) @PathVariable Long id) {
        Optional<Horario> horario = horarioService.getHorarioById(id);
        return horario.map(value -> ResponseEntity.ok().body(value))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear un nuevo horario", description = "Crea un nuevo horario con los datos proporcionados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Horario creado exitosamente",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Horario.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<Horario> createHorario(@RequestBody Horario horario) {
        Horario createdHorario = horarioService.createHorario(horario);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdHorario);
    }

    @Operation(summary = "Actualizar un horario existente", description = "Actualiza un horario existente con los datos proporcionados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horario actualizado exitosamente",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Horario.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Horario no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Horario> updateHorario(@Parameter(description = "ID del horario a actualizar", required = true) @PathVariable Long id,
                                                 @RequestBody Horario newHorarioData) {
        Horario updatedHorario = horarioService.updateHorario(id, newHorarioData);
        return updatedHorario != null ?
                ResponseEntity.ok().body(updatedHorario) :
                ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar un horario por su ID", description = "Elimina un horario existente por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Horario eliminado exitosamente"),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Horario no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHorario(@Parameter(description = "ID del horario a eliminar", required = true) @PathVariable Long id) {
        horarioService.deleteHorario(id);
        return ResponseEntity.noContent().build();
    }
}
