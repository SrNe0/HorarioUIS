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
import uis.horariouis.dto.DictadoDTO;
import uis.horariouis.exception.ResourceNotFoundException;
import uis.horariouis.model.Dictado;
import uis.horariouis.model.ErrorResponse;
import uis.horariouis.service.DictadoService;

import java.util.List;

@RestController
@RequestMapping("/api/dictados")
@Tag(name = "Dictados", description = "API para la gestión de dictados")
public class DictadoController {

    @Autowired
    private DictadoService dictadoService;

    @Operation(summary = "Obtener todos los dictados", description = "Obtiene una lista de todos los dictados disponibles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de dictados obtenida correctamente",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DictadoDTO.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping()
    public List<Dictado> getAllDictados() {
        return dictadoService.getAllDictados();
    }

    @Operation(summary = "Obtener un dictado por su ID", description = "Obtiene un dictado específico por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dictado encontrado",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DictadoDTO.class))}),
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

    @Operation(summary = "Eliminar un dictado por su ID", description = "Elimina un dictado existente por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Dictado eliminado exitosamente"),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Dictado no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDictado(@Parameter(description = "ID del dictado a eliminar", required = true) @PathVariable Long id) {
        try {
            dictadoService.deleteDictado(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Crear un nuevo dictado", description = "Crea un nuevo dictado a partir de los datos proporcionados en el DTO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dictado creado exitosamente",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DictadoDTO.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "El profesor o la asignatura no fueron encontrados",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/")
    public ResponseEntity<Dictado> createDictado(@Parameter(description = "Datos del nuevo dictado", required = true) @RequestBody DictadoDTO dictadoDTO) {
        try {
            Dictado nuevoDictado = dictadoService.createDictadoFromDTO(dictadoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDictado);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Actualizar un dictado existente", description = "Actualiza un dictado existente por su ID utilizando los datos proporcionados en el DTO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dictado actualizado exitosamente",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DictadoDTO.class))}),
            @ApiResponse(responseCode = "403", description = "No tiene permiso para acceder a este recurso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "El dictado, profesor o asignatura no fueron encontrados",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Dictado> updateDictado(@Parameter(description = "ID del dictado a actualizar", required = true) @PathVariable Long id,
                                                 @Parameter(description = "Datos actualizados del dictado", required = true) @RequestBody DictadoDTO dictadoDTO) {
        try {
            Dictado updatedDictado = dictadoService.updateDictado(id, dictadoDTO);
            return ResponseEntity.ok(updatedDictado);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
