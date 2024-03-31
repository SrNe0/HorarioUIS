package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.model.Accion;
import uis.horariouis.repository.AccionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AccionService {

    private final AccionRepository accionRepository;

    @Autowired
    public AccionService(AccionRepository accionRepository) {
        this.accionRepository = accionRepository;
    }

    public List<Accion> getAllAcciones() {
        return accionRepository.findAll();
    }

    public Optional<Accion> getAccionById(Long id) {
        return accionRepository.findById(id);
    }

    public Accion saveAccion(Accion accion) {
        return accionRepository.save(accion);
    }

    public void deleteAccionById(Long id) {
        accionRepository.deleteById(id);
    }
}
