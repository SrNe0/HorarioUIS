package uis.horariouis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uis.horariouis.dto.GrupoDTO;
import uis.horariouis.exception.ResourceNotFoundException;
import uis.horariouis.model.Grupo;
import uis.horariouis.service.GrupoService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/grupos")
public class GrupoController {

    @Autowired
    private GrupoService grupoService;

    @GetMapping("/")
    public List<Grupo> getAllGrupos() {
        return grupoService.getAllGrupos();
    }

    @GetMapping("/{id}")
    public Optional<Grupo> getGrupoById(@PathVariable Long id) {
        return grupoService.getGrupoById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteGrupo(@PathVariable Long id) {
        grupoService.deleteGrupo(id);
    }
    @PostMapping
    public ResponseEntity<Grupo> createGrupo(@RequestBody GrupoDTO grupoDTO) {
        Grupo grupo = grupoService.createGrupoFromDTO(grupoDTO);
        if (grupo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(grupo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Grupo> updateGrupo(@PathVariable Long id, @RequestBody GrupoDTO grupoDTO) {
        try {
            Grupo updatedGrupo = grupoService.updateGrupo(id, grupoDTO);
            return ResponseEntity.ok(updatedGrupo);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}