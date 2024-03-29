package uis.horariouis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uis.horariouis.model.Rol;
import uis.horariouis.service.RolService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    @GetMapping("/")
    public List<Rol> getAllRoles() {
        return rolService.getAllRoles();
    }

    @GetMapping("/{id}")
    public Optional<Rol> getRolById(@PathVariable Long id) {
        return rolService.getRolById(id);
    }

    @PostMapping("/")
    public Rol saveOrUpdateRol(@RequestBody Rol rol) {
        return rolService.saveOrUpdateRol(rol);
    }

    @DeleteMapping("/{id}")
    public void deleteRol(@PathVariable Long id) {
        rolService.deleteRol(id);
    }
}
