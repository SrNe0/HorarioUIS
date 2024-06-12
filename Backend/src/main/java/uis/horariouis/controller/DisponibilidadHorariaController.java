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
import uis.horariouis.model.DisponibilidadHoraria;
import uis.horariouis.model.ErrorResponse;
import uis.horariouis.service.DisponibilidadHorariaService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/disponibilidad-horaria")
@Tag(name = "DisponibilidadHoraria", description = "API para la gestión de disponibilidad horaria")
public class DisponibilidadHorariaController {

    @Autowired
    private DisponibilidadHorariaService disponibilidadHorariaService;

    @Operation(summary = "Obtener todas las disponibilidades horarias", description = "Devuelve una lista de todas las disponibilidades horarias disponibles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de disponibilidades horarias obtenida correctamente",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DisponibilidadHoraria.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public List<DisponibilidadHoraria> getAllDisponibilidadHoraria() {
        return disponibilidadHorariaService.getAllDisponibilidadesHorarias();
    }

    @Operation(summary = "Obtener una disponibilidad horaria por su ID", description = "Devuelve una disponibilidad horaria específica por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disponibilidad horaria encontrada",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DisponibilidadHoraria.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Disponibilidad horaria no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<DisponibilidadHoraria> getDisponibilidadHorariaById(@Parameter(description = "ID de la disponibilidad horaria a buscar", required = true) @PathVariable Long id) {
        DisponibilidadHoraria disponibilidadHoraria = disponibilidadHorariaService.getDisponibilidadHorariaById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidad horaria not found with id: " + id));
        return ResponseEntity.ok(disponibilidadHoraria);
    }

    @Operation(summary = "Crear una nueva disponibilidad horaria", description = "Crea una nueva disponibilidad horaria con los datos proporcionados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Disponibilidad horaria creada correctamente",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DisponibilidadHoraria.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<DisponibilidadHoraria> createDisponibilidadHoraria(@Valid @RequestBody DisponibilidadHoraria disponibilidadHoraria) {
        DisponibilidadHoraria nuevaDisponibilidadHoraria = disponibilidadHorariaService.saveDisponibilidadHoraria(disponibilidadHoraria);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaDisponibilidadHoraria);
    }

    @Operation(summary = "Actualizar una disponibilidad horaria existente", description = "Actualiza una disponibilidad horaria existente por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disponibilidad horaria actualizada correctamente",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DisponibilidadHoraria.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Disponibilidad horaria no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<DisponibilidadHoraria> updateDisponibilidadHoraria(@Parameter(description = "ID de la disponibilidad horaria a actualizar", required = true) @PathVariable Long id,
                                                                             @Valid @RequestBody DisponibilidadHoraria disponibilidadHoraria) {
        DisponibilidadHoraria disponibilidadHorariaActualizada = disponibilidadHorariaService.getDisponibilidadHorariaById(id)
                .map(disponibilidad -> {
                    disponibilidad.setDia(disponibilidadHoraria.getDia());
                    disponibilidad.setHoraInicio(disponibilidadHoraria.getHoraInicio());
                    disponibilidad.setHoraFin(disponibilidadHoraria.getHoraFin());
                    return disponibilidadHorariaService.saveDisponibilidadHoraria(disponibilidad);
                }).orElseThrow(() -> new ResourceNotFoundException("Disponibilidad horaria not found with id: " + id));

        return ResponseEntity.ok(disponibilidadHorariaActualizada);
    }

    @Operation(summary = "Eliminar una disponibilidad horaria por su ID", description = "Elimina una disponibilidad horaria existente por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Disponibilidad horaria eliminada correctamente"),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Disponibilidad horaria no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDisponibilidadHoraria(@Parameter(description = "ID de la disponibilidad horaria a eliminar", required = true) @PathVariable Long id) {
        disponibilidadHorariaService.deleteDisponibilidadHorariaById(id);
        return ResponseEntity.noContent().build();
    }
}
