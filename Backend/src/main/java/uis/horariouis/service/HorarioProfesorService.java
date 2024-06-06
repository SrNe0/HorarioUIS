package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.model.HorarioProfesor;
import uis.horariouis.repository.HorarioProfesorRepository;

import java.util.List;
import java.util.Optional;

@Service
public class HorarioProfesorService {

    private final HorarioProfesorRepository horarioProfesorRepository;

    @Autowired
    public HorarioProfesorService(HorarioProfesorRepository horarioProfesorRepository) {
        this.horarioProfesorRepository = horarioProfesorRepository;
    }

    public List<HorarioProfesor> getAllHorariosProfesores() {
        return horarioProfesorRepository.findAll();
    }

    public Optional<HorarioProfesor> getHorarioProfesorById(Long id) {
        return horarioProfesorRepository.findById(id);
    }

    public HorarioProfesor saveHorarioProfesor(HorarioProfesor horarioProfesor) {
        return horarioProfesorRepository.save(horarioProfesor);
    }

    public void deleteHorarioProfesorById(Long id) {
        horarioProfesorRepository.deleteById(id);
    }
}
