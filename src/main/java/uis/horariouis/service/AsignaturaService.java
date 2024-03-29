package uis.horariouis.service;

import  uis.horariouis.repository.AsignaturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.model.Asignatura;

import java.util.List;

@Service
public class AsignaturaService {

    @Autowired
    private AsignaturaRepository AsignaturaRepository;

    public List<Asignatura> findAll() {
        return AsignaturaRepository.findAll();
    }

    public Asignatura findById(Long id) {
        return AsignaturaRepository.findById(id).orElse(null);
    }

    public Asignatura save(Asignatura asignatura) {
        return AsignaturaRepository.save(asignatura);
    }

    public void deleteById(Long id) {
        AsignaturaRepository.deleteById(id);
    }
}

