package uis.horariouis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uis.horariouis.model.Edificio;
import uis.horariouis.service.EdificioService;

import java.util.List;
import java.util.Optional;

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
    public Optional<Edificio> getEdificioById(@PathVariable Long id) {
        return edificioService.getEdificioById(id);
    }

    @PostMapping("/")
    public Edificio saveOrUpdateEdificio(@RequestBody Edificio edificio) {
        return edificioService.saveOrUpdateEdificio(edificio);
    }

    @DeleteMapping("/{id}")
    public void deleteEdificio(@PathVariable Long id) {
        edificioService.deleteEdificio(id);
    }
}
