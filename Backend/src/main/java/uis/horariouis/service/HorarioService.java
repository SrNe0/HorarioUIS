package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.model.Horario;
import uis.horariouis.repository.HorarioRepository;

import java.util.List;
import java.util.Optional;

@Service
public class HorarioService {

    private final HorarioRepository horarioRepository;

    @Autowired
    public HorarioService(HorarioRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
    }

    // Método para obtener todos los horarios
    public List<Horario> getAllHorarios() {
        return horarioRepository.findAll();
    }

    // Método para obtener un horario por su ID
    public Optional<Horario> getHorarioById(Long id) {
        return horarioRepository.findById(id);
    }

    // Método para crear un nuevo horario
    public Horario createHorario(Horario horario) {
        // Verificar solapamiento de horarios

        return horarioRepository.save(horario);
    }

    // Método para actualizar un horario existente
    public Horario updateHorario(Long id, Horario newHorarioData) {
        return horarioRepository.findById(id)
                .map(horario -> {
                    horario.setDia(newHorarioData.getDia());
                    horario.setHoraInicio(newHorarioData.getHoraInicio());
                    horario.setHoraFin(newHorarioData.getHoraFin());
                    // Actualiza otros campos si es necesario
                    return horarioRepository.save(horario);
                })
                .orElse(null); // Manejo de error si no se encuentra el horario con el ID dado
    }

    // Método para eliminar un horario por su ID
    public void deleteHorario(Long id) {
        horarioRepository.deleteById(id);
    }

}
