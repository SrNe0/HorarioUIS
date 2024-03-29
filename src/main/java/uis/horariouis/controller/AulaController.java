package uis.horariouis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uis.horariouis.dto.AulaDTO;
import uis.horariouis.model.Aula;
import uis.horariouis.repository.AulaRepository;
import uis.horariouis.service.AulaService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/aulas")  // Quita la barra final
public class AulaController {

    @Autowired
    private AulaService aulaService;
    @Autowired
    private AulaRepository aulaRepository;

    @PostMapping("/create")  // Usa una ruta específica para crear aulas
    public ResponseEntity<Aula> createAula(@RequestBody AulaDTO aulaDTO) {
        Aula aulaCreada = aulaService.createOrUpdateAula(aulaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(aulaCreada);  // Retorna directamente la aula creada
    }

    @GetMapping
    public List<Aula> getAllAulas() {
        return aulaService.getAllAulas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aula> getAulaById(@PathVariable Long id) {
        return aulaService.getAulaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Si necesitas un endpoint para actualizar, considera usar PUT o PATCH
    @PutMapping("/{id}")
    public ResponseEntity<Aula> updateAula(@PathVariable Long id, @RequestBody AulaDTO aulaDTO) {
        return aulaService.updateAula(id, aulaDTO)
                .map(aulaActualizada -> ResponseEntity.ok().body(aulaActualizada))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAula(@PathVariable Long id) {
        boolean deleted = aulaService.deleteAula(id);
        if (deleted) {
            return ResponseEntity.ok().build();  // Devuelve 200 OK si la aula fue eliminada
        } else {
            return ResponseEntity.notFound().build();  // Devuelve 404 Not Found si la aula no fue encontrada
        }
    }
}

