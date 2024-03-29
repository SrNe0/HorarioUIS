package uis.horariouis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uis.horariouis.dto.GrupoDTO;
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

    @PostMapping("/")
    public Grupo saveOrUpdateGrupo(@RequestBody Grupo grupo) {
        return grupoService.saveOrUpdateGrupo(grupo);
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
}

