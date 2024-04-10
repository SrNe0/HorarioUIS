package uis.horariouis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uis.horariouis.exception.ResourceNotFoundException;
import uis.horariouis.model.HorarioProfesor;
import uis.horariouis.model.ErrorResponse;
import uis.horariouis.service.HorarioProfesorService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/horarios-profesores")
public class HorarioProfesorController {

    @Autowired
    private HorarioProfesorService horarioProfesorService;

    @GetMapping
    public List<HorarioProfesor> getAllHorariosProfesores() {
        return horarioProfesorService.getAllHorariosProfesores();
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioProfesor> getHorarioProfesorById(@PathVariable Long id) {
        HorarioProfesor horarioProfesor = horarioProfesorService.getHorarioProfesorById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario profesor not found with id: " + id));
        return ResponseEntity.ok(horarioProfesor);
    }

    @PostMapping
    public ResponseEntity<?> createHorarioProfesor(@Valid @RequestBody HorarioProfesor horarioProfesor) {
        HorarioProfesor nuevoHorarioProfesor = horarioProfesorService.saveHorarioProfesor(horarioProfesor);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoHorarioProfesor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HorarioProfesor> updateHorarioProfesor(@PathVariable Long id, @Valid @RequestBody HorarioProfesor horarioProfesor) {
        HorarioProfesor horarioProfesorActualizado = horarioProfesorService.getHorarioProfesorById(id)
                .map(horario -> {
                    horario.setProfesor(horarioProfesor.getProfesor());
                    horario.setDisponibilidadHoraria(horarioProfesor.getDisponibilidadHoraria());
                    return horarioProfesorService.saveHorarioProfesor(horario);
                }).orElseThrow(() -> new ResourceNotFoundException("Horario profesor not found with id: " + id));

        return ResponseEntity.ok(horarioProfesorActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHorarioProfesor(@PathVariable Long id) {
        horarioProfesorService.deleteHorarioProfesorById(id);
        return ResponseEntity.noContent().build();
    }
}
