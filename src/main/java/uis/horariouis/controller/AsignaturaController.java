package uis.horariouis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uis.horariouis.model.Asignatura;
import uis.horariouis.service.AsignaturaService;

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
    public Asignatura getAsignaturaById(@PathVariable Long id) {
        return asignaturaService.findById(id);
    }

    @PostMapping
    public Asignatura createAsignatura(@RequestBody Asignatura asignatura) {
        return asignaturaService.save(asignatura);
    }

    @PutMapping("/{id}")
    public Asignatura updateAsignatura(@PathVariable Long id, @RequestBody Asignatura asignatura) {
        // Verifica si la asignatura existe
        if (asignaturaService.findById(id) == null) {
            // Manejo de error o lanzar excepción
        }
        asignatura.setIdAsignatura(id);
        return asignaturaService.save(asignatura);
    }

    @DeleteMapping("/{id}")
    public void deleteAsignatura(@PathVariable Long id) {
        asignaturaService.deleteById(id);
    }
}

