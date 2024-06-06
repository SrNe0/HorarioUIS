package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.model.DisponibilidadHoraria;
import uis.horariouis.repository.DisponibilidadHorariaRepository;

import java.util.List;
import java.util.Optional;

@Service
public class DisponibilidadHorariaService {

    private final DisponibilidadHorariaRepository disponibilidadHorariaRepository;

    @Autowired
    public DisponibilidadHorariaService(DisponibilidadHorariaRepository disponibilidadHorariaRepository) {
        this.disponibilidadHorariaRepository = disponibilidadHorariaRepository;
    }

    public List<DisponibilidadHoraria> getAllDisponibilidadesHorarias() {
        return disponibilidadHorariaRepository.findAll();
    }

    public Optional<DisponibilidadHoraria> getDisponibilidadHorariaById(Long id) {
        return disponibilidadHorariaRepository.findById(id);
    }

    public DisponibilidadHoraria saveDisponibilidadHoraria(DisponibilidadHoraria disponibilidadHoraria) {
        return disponibilidadHorariaRepository.save(disponibilidadHoraria);
    }

    public void deleteDisponibilidadHorariaById(Long id) {
        disponibilidadHorariaRepository.deleteById(id);
    }
}
