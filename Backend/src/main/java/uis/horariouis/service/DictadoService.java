package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.model.Dictado;
import uis.horariouis.repository.DictadoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class DictadoService {

    private final DictadoRepository dictadoRepository;

    @Autowired
    public DictadoService(DictadoRepository dictadoRepository) {
        this.dictadoRepository = dictadoRepository;
    }

    public List<Dictado> getAllDictados() {
        return dictadoRepository.findAll();
    }

    public Optional<Dictado> getDictadoById(Long id) {
        return dictadoRepository.findById(id);
    }

    public Dictado saveDictado(Dictado dictado) {
        return dictadoRepository.save(dictado);
    }

    public void deleteDictadoById(Long id) {
        dictadoRepository.deleteById(id);
    }
}
