package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.model.Rol;
import uis.horariouis.repository.RolRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    // Método para obtener todos los roles
    public List<Rol> getAllRoles() {
        return rolRepository.findAll();  // Utiliza el método findAll() del repositorio JPA para obtener todos los roles
    }

    // Método para obtener un rol por su ID
    public Optional<Rol> getRolById(Long id) {
        return rolRepository.findById(id);  // Utiliza el método findById() del repositorio JPA para buscar un rol por ID
    }

    // Método para crear un nuevo rol
    public Rol createRol(Rol rol) {
        return rolRepository.save(rol);  // Guarda el rol en la base de datos
    }

    // Método para actualizar un rol existente
    public Optional<Rol> updateRol(Long id, Rol newRolData) {
        return rolRepository.findById(id).map(existingRol -> {
            existingRol.setNombreRol(newRolData.getNombreRol());
            // Puedes añadir más campos para actualizar aquí
            return rolRepository.save(existingRol);
        });
    }

    // Método para eliminar un rol
    public boolean deleteRol(Long id) {
        if (rolRepository.existsById(id)) {
            rolRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

