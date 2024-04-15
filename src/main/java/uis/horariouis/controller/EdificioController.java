package uis.horariouis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uis.horariouis.exception.ResourceNotFoundException;
import uis.horariouis.model.Edificio;
import uis.horariouis.service.EdificioService;

import java.util.List;


@RestController
@RequestMapping("/api/edificios")
public class EdificioController {

    @Autowired
    private EdificioService edificioService;

    @GetMapping("/")
    public List<Edificio> getAllEdificios() {
        return edificioService.getAllEdificios();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Edificio> getEdificioById(@PathVariable Long id) {
        return edificioService.getEdificioById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public ResponseEntity<Edificio> createEdificio(@RequestBody Edificio edificio) {
        Edificio newEdificio = edificioService.createEdificio(edificio);
        return ResponseEntity.status(HttpStatus.CREATED).body(newEdificio);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Edificio> updateEdificio(@PathVariable Long id, @RequestBody Edificio edificio) {
        try {
            Edificio updatedEdificio = edificioService.updateEdificio(id, edificio);
            return ResponseEntity.ok(updatedEdificio);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public void deleteEdificio(@PathVariable Long id) {
        edificioService.deleteEdificio(id);
    }
}
