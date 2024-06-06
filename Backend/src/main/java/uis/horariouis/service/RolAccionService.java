package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.model.RolAccion;
import uis.horariouis.repository.RolAccionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RolAccionService {

    private final RolAccionRepository rolAccionRepository;

    @Autowired
    public RolAccionService(RolAccionRepository rolAccionRepository) {
        this.rolAccionRepository = rolAccionRepository;
    }

    public List<RolAccion> getAllRolAcciones() {
        return rolAccionRepository.findAll();
    }

    public Optional<RolAccion> getRolAccionById(Long id) {
        return rolAccionRepository.findById(id);
    }

    public RolAccion saveRolAccion(RolAccion rolAccion) {
        return rolAccionRepository.save(rolAccion);
    }

    public void deleteRolAccionById(Long id) {
        rolAccionRepository.deleteById(id);
    }
}
