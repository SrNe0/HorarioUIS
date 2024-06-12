package uis.horariouis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uis.horariouis.exception.ResourceNotFoundException;
import uis.horariouis.model.Accion;
import uis.horariouis.service.AccionService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/acciones")
public class AccionController {

    @Autowired
    private AccionService accionService;

    @GetMapping
    public List<Accion> getAllAcciones() {
        return accionService.getAllAcciones();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Accion> getAccionById(@PathVariable Long id) {
        Accion accion = accionService.getAccionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Accion not found with id: " + id));
        return ResponseEntity.ok(accion);
    }

    @PostMapping
    public ResponseEntity<?> createAccion(@Valid @RequestBody Accion accion) {
        Accion nuevaAccion = accionService.saveAccion(accion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaAccion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Accion> updateAccion(@PathVariable Long id, @Valid @RequestBody Accion accion) {
        Accion accionActualizada = accionService.saveAccion(accion);
        return ResponseEntity.ok(accionActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccion(@PathVariable Long id) {
        accionService.deleteAccionById(id);
        return ResponseEntity.noContent().build();
    }
}
