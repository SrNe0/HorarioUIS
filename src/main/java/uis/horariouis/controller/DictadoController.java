package uis.horariouis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uis.horariouis.exception.ResourceNotFoundException;
import uis.horariouis.model.Dictado;
import uis.horariouis.model.ErrorResponse;
import uis.horariouis.service.DictadoService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/dictados")
public class DictadoController {

    @Autowired
    private DictadoService dictadoService;

    @GetMapping
    public List<Dictado> getAllDictados() {
        return dictadoService.getAllDictados();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dictado> getDictadoById(@PathVariable Long id) {
        Dictado dictado = dictadoService.getDictadoById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dictado not found with id: " + id));
        return ResponseEntity.ok(dictado);
    }

    @PostMapping
    public ResponseEntity<?> createDictado(@Valid @RequestBody Dictado dictado) {
        Dictado nuevoDictado = dictadoService.saveDictado(dictado);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDictado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dictado> updateDictado(@PathVariable Long id, @Valid @RequestBody Dictado dictado) {
        Dictado dictadoActualizado = dictadoService.saveDictado(dictado);
        return ResponseEntity.ok(dictadoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDictado(@PathVariable Long id) {
        dictadoService.deleteDictadoById(id);
        return ResponseEntity.noContent().build();
    }
}
