package uis.horariouis.controller;

// Importación de las clases necesarias de Spring Framework y de otras dependencias.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uis.horariouis.model.Rol;
import uis.horariouis.service.RolService;

import java.util.List;

// Anotación para marcar esta clase como un controlador REST.
@RestController
// Ruta base para todos los endpoints en este controlador.
@RequestMapping("/api/roles")
public class RolController {

    // Inyección de la dependencia del servicio de roles.
    @Autowired
    private RolService rolService;

    // Endpoint para obtener todos los roles, mapeado a una petición GET.
    @GetMapping("/")
    public List<Rol> getAllRoles() {
        // Devuelve la lista de todos los roles.
        return rolService.getAllRoles();
    }

    // Endpoint para obtener un rol por su ID, mapeado a GET con un parámetro de ruta.
    @GetMapping("/{id}")
    public ResponseEntity<Rol> getRolById(@PathVariable Long id) {
        // Intenta obtener el rol por su ID y maneja el caso en que no se encuentre.
        return rolService.getRolById(id)
                .map(ResponseEntity::ok)  // Si se encuentra, devuelve el rol con código 200 OK.
                .orElse(ResponseEntity.notFound().build());  // Si no se encuentra, devuelve 404 Not Found.
    }

    // Endpoint para crear un nuevo rol, mapeado a POST.
    @PostMapping("/")
    public ResponseEntity<Rol> createRol(@RequestBody Rol rol) {
        // Crea el nuevo rol y devuelve una respuesta con el rol creado y código 200 OK.
        Rol newRol = rolService.createRol(rol);
        return ResponseEntity.ok(newRol);
    }

    // Endpoint para actualizar un rol existente, mapeado a PUT con un parámetro de ruta.
    @PutMapping("/{id}")
    public ResponseEntity<Rol> updateRol(@PathVariable Long id, @RequestBody Rol rol) {
        // Intenta actualizar el rol por su ID y maneja el caso en que no se encuentre.
        return rolService.updateRol(id, rol)
                .map(ResponseEntity::ok)  // Si se actualiza correctamente, devuelve el rol actualizado con código 200 OK.
                .orElse(ResponseEntity.notFound().build());  // Si no se encuentra, devuelve 404 Not Found.
    }

    // Endpoint para eliminar un rol por su ID, mapeado a DELETE.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRol(@PathVariable Long id) {
        // Intenta eliminar el rol por su ID y devuelve una respuesta adecuada.
        if (rolService.deleteRol(id)) {
            return ResponseEntity.ok().build();  // Si se elimina correctamente, devuelve 200 OK.
        } else {
            return ResponseEntity.notFound().build();  // Si no se encuentra, devuelve 404 Not Found.
        }
    }
}
