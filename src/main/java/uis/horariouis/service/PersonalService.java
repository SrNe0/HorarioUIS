package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.model.Personal;
import uis.horariouis.repository.PersonalRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PersonalService {

    @Autowired
    private PersonalRepository personalRepository;

    public List<Personal> getAllPersonal() {
        return personalRepository.findAll();
    }

    public Optional<Personal> getPersonalById(Long id) {
        return personalRepository.findById(id);
    }

    public Personal saveOrUpdatePersonal(Personal personal) {
        return personalRepository.save(personal);
    }

    public void deletePersonal(Long id) {
        personalRepository.deleteById(id);
    }
}
