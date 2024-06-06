package uis.horariouis.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

// Importación de las clases necesarias de Spring Framework y de otras dependencias.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uis.horariouis.model.Rol;
import uis.horariouis.service.RolService;

import java.util.List;

// Anotación para marcar esta clase como un controlador REST.
@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "API para la gestión de roles")
public class RolController {

    @Autowired
    private RolService rolService;

    @Operation(summary = "Obtener todos los roles", description = "Devuelve una lista de todos los roles disponibles.")
    @ApiResponse(responseCode = "200", description = "Lista de roles encontrada",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Rol.class)))
    @GetMapping("/")
    public List<Rol> getAllRoles() {
        return rolService.getAllRoles();
    }

    @Operation(summary = "Obtener un rol por ID", description = "Devuelve un rol específico por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Rol.class))),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Rol> getRolById(@PathVariable Long id) {
        return rolService.getRolById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear un nuevo rol", description = "Crea un nuevo rol con la información proporcionada.")
    @ApiResponse(responseCode = "200", description = "Rol creado correctamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Rol.class)))
    @PostMapping("/")
    public ResponseEntity<Rol> createRol(@RequestBody Rol rol) {
        Rol newRol = rolService.createRol(rol);
        return ResponseEntity.ok(newRol);
    }

    @Operation(summary = "Actualizar un rol existente", description = "Actualiza la información de un rol existente basándose en su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol actualizado correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Rol.class))),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Rol> updateRol(@PathVariable Long id, @RequestBody Rol rol) {
        return rolService.updateRol(id, rol)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar un rol", description = "Elimina un rol basándose en su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRol(@PathVariable Long id) {
        if (rolService.deleteRol(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
