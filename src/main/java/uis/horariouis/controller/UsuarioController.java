package uis.horariouis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uis.horariouis.exception.ResourceNotFoundException;
import uis.horariouis.model.Usuario;
import uis.horariouis.model.ErrorResponse;
import uis.horariouis.service.UsuarioService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> getAllUsuarios() {
        return usuarioService.getAllUsuarios();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        Usuario usuario = usuarioService.getUsuarioById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario not found with id: " + id));
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<?> createUsuario(@Valid @RequestBody Usuario usuario) {
        Usuario nuevoUsuario = usuarioService.saveUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        Usuario usuarioActualizado = usuarioService.getUsuarioById(id)
                .map(user -> {
                    user.setNombreUsuario(usuario.getNombreUsuario());
                    user.setContrasena(usuario.getContrasena());
                    user.setRol(usuario.getRol());
                    return usuarioService.saveUsuario(user);
                }).orElseThrow(() -> new ResourceNotFoundException("Usuario not found with id: " + id));

        return ResponseEntity.ok(usuarioActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable Long id) {
        usuarioService.deleteUsuarioById(id);
        return ResponseEntity.noContent().build();
    }
}
