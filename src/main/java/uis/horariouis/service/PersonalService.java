package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.model.Profesor;
import uis.horariouis.repository.PersonalRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PersonalService {

    @Autowired
    private PersonalRepository personalRepository;

    public List<Profesor> getAllPersonal() {
        return personalRepository.findAll();
    }

    public Optional<Profesor> getPersonalById(Long id) {
        return personalRepository.findById(id);
    }

    public Profesor saveOrUpdatePersonal(Profesor profesor) {
        return personalRepository.save(profesor);
    }

    public void deletePersonal(Long id) {
        personalRepository.deleteById(id);
    }
}
