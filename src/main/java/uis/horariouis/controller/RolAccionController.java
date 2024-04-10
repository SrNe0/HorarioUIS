package uis.horariouis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uis.horariouis.exception.ResourceNotFoundException;
import uis.horariouis.model.RolAccion;
import uis.horariouis.model.ErrorResponse;
import uis.horariouis.service.RolAccionService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/rol-acciones")
public class RolAccionController {

    @Autowired
    private RolAccionService rolAccionService;

    @GetMapping
    public List<RolAccion> getAllRolAcciones() {
        return rolAccionService.getAllRolAcciones();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolAccion> getRolAccionById(@PathVariable Long id) {
        RolAccion rolAccion = rolAccionService.getRolAccionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol-acción not found with id: " + id));
        return ResponseEntity.ok(rolAccion);
    }

    @PostMapping
    public ResponseEntity<?> createRolAccion(@Valid @RequestBody RolAccion rolAccion) {
        RolAccion nuevoRolAccion = rolAccionService.saveRolAccion(rolAccion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoRolAccion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RolAccion> updateRolAccion(@PathVariable Long id, @Valid @RequestBody RolAccion rolAccion) {
        RolAccion rolAccionActualizado = rolAccionService.getRolAccionById(id)
                .map(rol -> {
                    rol.setRol(rolAccion.getRol());
                    rol.setAccion(rolAccion.getAccion());
                    // Si hay más campos, actualízalos aquí
                    return rolAccionService.saveRolAccion(rol);
                }).orElseThrow(() -> new ResourceNotFoundException("Rol-acción not found with id: " + id));

        return ResponseEntity.ok(rolAccionActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRolAccion(@PathVariable Long id) {
        rolAccionService.deleteRolAccionById(id);
        return ResponseEntity.noContent().build();
    }
}
