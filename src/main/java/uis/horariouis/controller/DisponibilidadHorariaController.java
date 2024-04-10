package uis.horariouis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uis.horariouis.exception.ResourceNotFoundException;
import uis.horariouis.model.DisponibilidadHoraria;
import uis.horariouis.model.ErrorResponse;
import uis.horariouis.service.DisponibilidadHorariaService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/disponibilidad-horaria")
public class DisponibilidadHorariaController {

    @Autowired
    private DisponibilidadHorariaService disponibilidadHorariaService;

    @GetMapping
    public List<DisponibilidadHoraria> getAllDisponibilidadHoraria() {
        return disponibilidadHorariaService.getAllDisponibilidadesHorarias();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisponibilidadHoraria> getDisponibilidadHorariaById(@PathVariable Long id) {
        DisponibilidadHoraria disponibilidadHoraria = disponibilidadHorariaService.getDisponibilidadHorariaById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidad horaria not found with id: " + id));
        return ResponseEntity.ok(disponibilidadHoraria);
    }

    @PostMapping
    public ResponseEntity<?> createDisponibilidadHoraria(@Valid @RequestBody DisponibilidadHoraria disponibilidadHoraria) {
        DisponibilidadHoraria nuevaDisponibilidadHoraria = disponibilidadHorariaService.saveDisponibilidadHoraria(disponibilidadHoraria);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaDisponibilidadHoraria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DisponibilidadHoraria> updateDisponibilidadHoraria(@PathVariable Long id, @Valid @RequestBody DisponibilidadHoraria disponibilidadHoraria) {
        DisponibilidadHoraria disponibilidadHorariaActualizada = disponibilidadHorariaService.getDisponibilidadHorariaById(id)
                .map(disponibilidad -> {
                    disponibilidad.setDia(disponibilidadHoraria.getDia());
                    disponibilidad.setHoraInicio(disponibilidadHoraria.getHoraInicio());
                    disponibilidad.setHoraFin(disponibilidadHoraria.getHoraFin());
                    return disponibilidadHorariaService.saveDisponibilidadHoraria(disponibilidad);
                }).orElseThrow(() -> new ResourceNotFoundException("Disponibilidad horaria not found with id: " + id));

        return ResponseEntity.ok(disponibilidadHorariaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDisponibilidadHoraria(@PathVariable Long id) {
        disponibilidadHorariaService.deleteDisponibilidadHorariaById(id);
        return ResponseEntity.noContent().build();
    }
}
