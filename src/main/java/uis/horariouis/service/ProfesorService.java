package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.model.Profesor;
import uis.horariouis.repository.ProfesorRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProfesorService {

    private final ProfesorRepository profesorRepository;

    @Autowired
    public ProfesorService(ProfesorRepository profesorRepository) {
        this.profesorRepository = profesorRepository;
    }

    public List<Profesor> getAllProfesores() {
        return profesorRepository.findAll();
    }

    public Optional<Profesor> getProfesorById(Long id) {
        return profesorRepository.findById(id);
    }

    public Profesor createProfesor(Profesor profesor) {
        return profesorRepository.save(profesor);
    }

    public Profesor updateProfesor(Long id, Profesor profesorDetails) {
        Profesor profesor = profesorRepository.findById(id).orElseThrow(() -> new RuntimeException("Profesor no encontrado"));
        profesor.setDocumentoIdentidad(profesorDetails.getDocumentoIdentidad());
        profesor.setApellidos(profesorDetails.getApellidos());
        profesor.setNombres(profesorDetails.getNombres());
        profesor.setTelefono(profesorDetails.getTelefono());
        profesor.setCorreo(profesorDetails.getCorreo());
        return profesorRepository.save(profesor);
    }

    public void deleteProfesor(Long id) {
        profesorRepository.deleteById(id);
    }
}
