package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.model.Edificio;
import uis.horariouis.repository.EdificioRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EdificioService {

    @Autowired
    private EdificioRepository edificioRepository;

    public List<Edificio> getAllEdificios() {
        return edificioRepository.findAll();
    }

    public Optional<Edificio> getEdificioById(Long id) {
        return edificioRepository.findById(id);
    }

    public Edificio saveOrUpdateEdificio(Edificio edificio) {
        return edificioRepository.save(edificio);
    }

    public void deleteEdificio(Long id) {
        edificioRepository.deleteById(id);
    }
}
