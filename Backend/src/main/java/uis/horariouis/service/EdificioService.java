package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.dto.EdificioDTO;
import uis.horariouis.exception.ResourceNotFoundException;
import uis.horariouis.model.Edificio;
import uis.horariouis.repository.EdificioRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EdificioService {

    @Autowired
    private EdificioRepository edificioRepository;

    // Obtener todos los edificios
    public List<Edificio> getAllEdificios() {
        return edificioRepository.findAll();
    }

    // Obtener un edificio por ID
    public Optional<Edificio> getEdificioById(Long id) {
        return edificioRepository.findById(id);
    }

    // Crear un nuevo edificio utilizando el DTO
    public Edificio createEdificio(EdificioDTO edificioDTO) {
        // Convertir el DTO a la entidad Edificio
        Edificio edificio = new Edificio();
        edificio.setNombre(edificioDTO.getNombre());  // Asigna el nombre del DTO a la entidad

        return edificioRepository.save(edificio);  // Guarda el edificio en la base de datos
    }

    // Actualizar un edificio existente utilizando el DTO
    public Edificio updateEdificio(Long id, EdificioDTO edificioDTO) {
        return edificioRepository.findById(id).map(existingEdificio -> {
            // Actualiza los campos del edificio existente con los datos del DTO
            existingEdificio.setNombre(edificioDTO.getNombre());
            return edificioRepository.save(existingEdificio);
        }).orElseThrow(() -> new ResourceNotFoundException("Edificio no encontrado con el id: " + id));
    }

    // Eliminar un edificio por su ID
    public void deleteEdificio(Long id) {
        edificioRepository.deleteById(id);
    }
}
