package uis.horariouis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uis.horariouis.exception.ResourceNotFoundException;
import uis.horariouis.model.Asignatura;
import uis.horariouis.model.ErrorResponse;
import uis.horariouis.service.AsignaturaService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/asignaturas")
public class AsignaturaController {

    @Autowired
    private AsignaturaService asignaturaService;

    @GetMapping
    public List<Asignatura> getAllAsignaturas() {
        return asignaturaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asignatura> getAsignaturaById(@PathVariable Long id) {
        Asignatura asignatura = asignaturaService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asignatura not found with id: " + id));
        return ResponseEntity.ok(asignatura);
    }

    @PostMapping
    public ResponseEntity<?> createAsignatura(@Valid @RequestBody Asignatura asignatura) {
        try {
            Asignatura nuevaAsignatura = asignaturaService.save(asignatura);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaAsignatura);
        } catch (IllegalArgumentException ex) {
            ErrorResponse errorResponse = new ErrorResponse("Validation error", HttpStatus.BAD_REQUEST.value());
            errorResponse.setDetails(Collections.singletonMap("message", ex.getMessage()));
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Asignatura> updateAsignatura(@PathVariable Long id, @Valid @RequestBody Asignatura asignatura) {
        Asignatura asignaturaActualizada = asignaturaService.update(id, asignatura);
        return ResponseEntity.ok(asignaturaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAsignatura(@PathVariable Long id) {
        try {
            asignaturaService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException ex) {
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }


}
