package uis.horariouis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
// Importaciones necesarias de Spring Framework y otras dependencias.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uis.horariouis.exception.ResourceNotFoundException;
import uis.horariouis.model.Usuario;
import uis.horariouis.service.UsuarioService;

import javax.validation.Valid;
import java.util.List;

// Marca esta clase como un controlador REST.
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "APIs para la gestión de usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Obtener todos los usuarios", description = "Retorna una lista de todos los usuarios registrados.")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida correctamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Usuario.class)))
    @GetMapping
    public List<Usuario> getAllUsuarios() {
        return usuarioService.getAllUsuarios();
    }

    @Operation(summary = "Obtener un usuario por ID", description = "Retorna un usuario específico por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@Parameter(description = "ID del usuario que se busca", required = true) @PathVariable Long id) {
        Usuario usuario = usuarioService.getUsuarioById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario not found with id: " + id));
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Crear un nuevo usuario", description = "Crea un nuevo usuario con los datos proporcionados.")
    @ApiResponse(responseCode = "201", description = "Usuario creado correctamente")
    @PostMapping
    public ResponseEntity<?> createUsuario(@Valid @RequestBody Usuario usuario) {
        Usuario nuevoUsuario = usuarioService.saveUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    @Operation(summary = "Actualizar un usuario existente", description = "Actualiza los datos de un usuario existente basado en su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(@Parameter(description = "ID del usuario que se actualizará", required = true) @PathVariable Long id,
                                                 @Valid @RequestBody Usuario usuario) {
        Usuario usuarioActualizado = usuarioService.getUsuarioById(id)
                .map(user -> {
                    user.setNombreUsuario(usuario.getNombreUsuario());
                    user.setContrasena(usuario.getContrasena());
                    user.setRol(usuario.getRol());
                    return usuarioService.saveUsuario(user);
                }).orElseThrow(() -> new ResourceNotFoundException("Usuario not found with id: " + id));

        return ResponseEntity.ok(usuarioActualizado);
    }

    @Operation(summary = "Eliminar un usuario", description = "Elimina un usuario basado en su ID.")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(@Parameter(description = "ID del usuario que será eliminado", required = true) @PathVariable Long id) {
        usuarioService.deleteUsuarioById(id);
        return ResponseEntity.noContent().build();
    }
}
