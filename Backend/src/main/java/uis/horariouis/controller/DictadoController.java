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
import uis.horariouis.model.Dictado;
import uis.horariouis.model.ErrorResponse;
import uis.horariouis.service.DictadoService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/dictados")
@Tag(name = "Dictados", description = "API para la gestión de dictados, recibe la una materia y un profesor (para quitar" +
        "relacion muchos a mcuhos))")
public class DictadoController {

    @Autowired
    private DictadoService dictadoService;

    @Operation(summary = "Obtener todos los dictados", description = "Devuelve una lista de todos los dictados disponibles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de dictados obtenida correctamente",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Dictado.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public List<Dictado> getAllDictados() {
        return dictadoService.getAllDictados();
    }

    @Operation(summary = "Obtener un dictado por su ID", description = "Devuelve un dictado específico por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dictado encontrado",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Dictado.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Dictado no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Dictado> getDictadoById(@Parameter(description = "ID del dictado a buscar", required = true) @PathVariable Long id) {
        Dictado dictado = dictadoService.getDictadoById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dictado not found with id: " + id));
        return ResponseEntity.ok(dictado);
    }

    @Operation(summary = "Crear un nuevo dictado", description = "Crea un nuevo dictado con los datos proporcionados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dictado creado correctamente",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Dictado.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<Dictado> createDictado(@Valid @RequestBody Dictado dictado) {
        Dictado nuevoDictado = dictadoService.saveDictado(dictado);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDictado);
    }

    @Operation(summary = "Actualizar un dictado existente", description = "Actualiza un dictado existente con los datos proporcionados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dictado actualizado correctamente",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Dictado.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Dictado no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Dictado> updateDictado(@Parameter(description = "ID del dictado a actualizar", required = true) @PathVariable Long id,
                                                 @Valid @RequestBody Dictado dictado) {
        Dictado dictadoActualizado = dictadoService.saveDictado(dictado);
        return ResponseEntity.ok(dictadoActualizado);
    }

    @Operation(summary = "Eliminar un dictado por su ID", description = "Elimina un dictado existente por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Dictado eliminado correctamente"),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Dictado no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDictado(@Parameter(description = "ID del dictado a eliminar", required = true) @PathVariable Long id) {
        dictadoService.deleteDictadoById(id);
        return ResponseEntity.noContent().build();
    }
}
