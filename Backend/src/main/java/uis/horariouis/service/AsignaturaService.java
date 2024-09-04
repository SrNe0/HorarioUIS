package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.dto.AsignaturaDTO;
import uis.horariouis.exception.ResourceNotFoundException;
import uis.horariouis.model.Asignatura;
import uis.horariouis.repository.AsignaturaRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AsignaturaService {

    @Autowired
    private AsignaturaRepository asignaturaRepository;

    // Obtener todas las asignaturas
    public List<Asignatura> findAll() {
        return asignaturaRepository.findAll();
    }

    // Obtener una asignatura por su ID
    public Optional<Asignatura> findById(Long id) {
        return asignaturaRepository.findById(id);
    }

    // Crear una nueva asignatura usando el DTO
    public Asignatura save(AsignaturaDTO asignaturaDTO) {
        // Validar los datos de entrada desde el DTO
        if (asignaturaDTO.getCodigo() == null || asignaturaDTO.getCodigo().isEmpty()) {
            throw new IllegalArgumentException("El código de la asignatura es obligatorio");
        }
        if (asignaturaDTO.getNombre() == null || asignaturaDTO.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la asignatura es obligatorio");
        }

        // Mapear el DTO a la entidad Asignatura
        Asignatura asignatura = new Asignatura();
        asignatura.setCodigo(asignaturaDTO.getCodigo());
        asignatura.setNombre(asignaturaDTO.getNombre());
        asignatura.setHorasTeoria(asignaturaDTO.getHorasTeoria());
        asignatura.setHorasPractica(asignaturaDTO.getHorasPractica());
        asignatura.setNecesitaComputadores(asignaturaDTO.getNecesitaComputadores());

        // Guardar la asignatura
        return asignaturaRepository.save(asignatura);
    }

    // Actualizar una asignatura existente usando el DTO
    public Asignatura update(Long id, AsignaturaDTO asignaturaDTO) {
        return asignaturaRepository.findById(id)
                .map(asignaturaExistente -> {
                    // Actualizar los datos de la asignatura existente con los del DTO
                    asignaturaExistente.setCodigo(asignaturaDTO.getCodigo());
                    asignaturaExistente.setNombre(asignaturaDTO.getNombre());
                    asignaturaExistente.setHorasTeoria(asignaturaDTO.getHorasTeoria());
                    asignaturaExistente.setHorasPractica(asignaturaDTO.getHorasPractica());
                    asignaturaExistente.setNecesitaComputadores(asignaturaDTO.getNecesitaComputadores());
                    return asignaturaRepository.save(asignaturaExistente);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Asignatura no encontrada con id: " + id));
    }

    // Eliminar una asignatura por su ID
    public void deleteById(Long id) {
        // Verificar si la asignatura existe antes de eliminarla
        if (!asignaturaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Asignatura no encontrada con id: " + id);
        }
        asignaturaRepository.deleteById(id);
    }
}
