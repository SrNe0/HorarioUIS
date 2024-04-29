package uis.horariouis.controller;

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
// Define la ruta base para todos los endpoints en este controlador.
@RequestMapping("/api/usuarios")
public class UsuarioController {

    // Inyección de la dependencia del servicio de usuario.
    @Autowired
    private UsuarioService usuarioService;

    // Endpoint para obtener todos los usuarios, mapeado a una petición GET.
    @GetMapping
    public List<Usuario> getAllUsuarios() {
        // Devuelve la lista de todos los usuarios.
        return usuarioService.getAllUsuarios();
    }

    // Endpoint para obtener un usuario por su ID, mapeado a GET con un parámetro de ruta.
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        // Intenta obtener el usuario por su ID y maneja el caso en que no se encuentre.
        Usuario usuario = usuarioService.getUsuarioById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario not found with id: " + id));
        return ResponseEntity.ok(usuario);
    }

    // Endpoint para crear un nuevo usuario, mapeado a POST.
    @PostMapping
    public ResponseEntity<?> createUsuario(@Valid @RequestBody Usuario usuario) {
        // Crea el nuevo usuario y devuelve una respuesta con el usuario creado y código 201 Created.
        Usuario nuevoUsuario = usuarioService.saveUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    // Endpoint para actualizar un usuario existente, mapeado a PUT con un parámetro de ruta.
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        // Intenta actualizar el usuario por su ID y maneja el caso en que no se encuentre.
        Usuario usuarioActualizado = usuarioService.getUsuarioById(id)
                .map(user -> {
                    // Actualiza los campos del usuario con los valores proporcionados.
                    user.setNombreUsuario(usuario.getNombreUsuario());
                    user.setContrasena(usuario.getContrasena());
                    user.setRol(usuario.getRol());
                    return usuarioService.saveUsuario(user);
                }).orElseThrow(() -> new ResourceNotFoundException("Usuario not found with id: " + id));

        return ResponseEntity.ok(usuarioActualizado);
    }

    // Endpoint para eliminar un usuario por su ID, mapeado a DELETE.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable Long id) {
        // Elimina el usuario por su ID y devuelve una respuesta adecuada.
        usuarioService.deleteUsuarioById(id);
        return ResponseEntity.noContent().build();
    }
}
