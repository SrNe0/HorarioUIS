package uis.horariouis.service;

import uis.horariouis.exception.ResourceNotFoundException;
import uis.horariouis.repository.AsignaturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.model.Asignatura;

import java.util.List;
import java.util.Optional;

@Service
public class AsignaturaService {

    @Autowired
    private AsignaturaRepository asignaturaRepository;

    public List<Asignatura> findAll() {
        return asignaturaRepository.findAll();
    }

    public Optional<Asignatura> findById(Long id) {
        return asignaturaRepository.findById(id);
    }

    public Asignatura save(Asignatura asignatura) {
        // Validar los datos de entrada
        if (asignatura.getCodigo() == null || asignatura.getCodigo().isEmpty()) {
            throw new IllegalArgumentException("El código de la asignatura es obligatorio");
        }
        if (asignatura.getNombre() == null || asignatura.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la asignatura es obligatorio");
        }
        // Guardar la asignatura
        return asignaturaRepository.save(asignatura);
    }

    public Asignatura update(Long id, Asignatura asignatura) {
        // Verificar si la asignatura existe
        Optional<Asignatura> optionalAsignatura = asignaturaRepository.findById(id);
        if (optionalAsignatura.isPresent()) {
            Asignatura asignaturaExistente = optionalAsignatura.get();
            // Actualizar los campos de la asignatura existente
            asignaturaExistente.setCodigo(asignatura.getCodigo());
            asignaturaExistente.setNombre(asignatura.getNombre());
            asignaturaExistente.setHorasTeoria(asignatura.getHorasTeoria());
            asignaturaExistente.setHorasPractica(asignatura.getHorasPractica());

            // Guardar la asignatura actualizada
            return asignaturaRepository.save(asignaturaExistente);
        } else {
            throw new ResourceNotFoundException("Asignatura not found with id: " + id);
        }
    }


    public void deleteById(Long id) {
        // Verificar si la asignatura existe antes de eliminarla
        findById(id);
        asignaturaRepository.deleteById(id);
    }
}
