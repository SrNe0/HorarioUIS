package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.exception.ResourceNotFoundException;
import uis.horariouis.model.Edificio;
import uis.horariouis.repository.EdificioRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EdificioService {

    @Autowired
    private EdificioRepository edificioRepository;

    public List<Edificio> getAllEdificios() {
        return edificioRepository.findAll();
    }

    public Optional<Edificio> getEdificioById(Long id) {
        return edificioRepository.findById(id);
    }

    // Método para crear un nuevo edificio
    public Edificio createEdificio(Edificio edificio) {
        return edificioRepository.save(edificio);  // Guarda un nuevo edificio
    }

    // Método para actualizar un edificio existente
    public Edificio updateEdificio(Long id, Edificio edificio) {
        return edificioRepository.findById(id).map(existingEdificio -> {
            existingEdificio.setNombre(edificio.getNombre());
            // Aquí se pueden agregar más campos para actualizar
            return edificioRepository.save(existingEdificio);
        }).orElseThrow(() -> new ResourceNotFoundException("Edificio not found with id: " + id));
    }
    public void deleteEdificio(Long id) {
        edificioRepository.deleteById(id);
    }
}
