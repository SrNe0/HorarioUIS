package uis.horariouis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uis.horariouis.model.Rol;
import uis.horariouis.service.RolService;

import java.util.List;

@RestController  // Marca la clase como un controlador REST
@RequestMapping("/api/roles")  // Define la ruta base para todos los endpoints en este controlador
public class RolController {

    @Autowired
    private RolService rolService;  // Inyección de la dependencia del servicio de roles

    // Endpoint para obtener todos los roles
    @GetMapping("/")
    public List<Rol> getAllRoles() {
        return rolService.getAllRoles();
    }

    // Endpoint para obtener un rol por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Rol> getRolById(@PathVariable Long id) {
        return rolService.getRolById(id)
                .map(ResponseEntity::ok)  // Si el rol existe, devuelve el rol con código 200
                .orElse(ResponseEntity.notFound().build());  // Si no, devuelve 404 Not Found
    }

    // Endpoint para crear un nuevo rol
    @PostMapping("/")
    public ResponseEntity<Rol> createRol(@RequestBody Rol rol) {
        Rol newRol = rolService.createRol(rol);
        return ResponseEntity.ok(newRol);  // Devuelve el rol creado con código 200
    }

    // Endpoint para actualizar un rol existente
    @PutMapping("/{id}")
    public ResponseEntity<Rol> updateRol(@PathVariable Long id, @RequestBody Rol rol) {
        return rolService.updateRol(id, rol)
                .map(ResponseEntity::ok)  // Devuelve el rol actualizado con código 200
                .orElse(ResponseEntity.notFound().build());  // Si no encuentra el rol, devuelve 404 Not Found
    }

    // Endpoint para eliminar un rol por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRol(@PathVariable Long id) {
        if (rolService.deleteRol(id)) {
            return ResponseEntity.ok().build();  // Si el rol se elimina correctamente, devuelve 200 OK
        } else {
            return ResponseEntity.notFound().build();  // Si no se encuentra el rol, devuelve 404 Not Found
        }
    }
}
