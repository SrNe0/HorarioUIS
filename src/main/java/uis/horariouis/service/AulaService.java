package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.dto.AulaDTO;
import uis.horariouis.model.Aula;
import uis.horariouis.model.Edificio;
import uis.horariouis.repository.AulaRepository;
import uis.horariouis.repository.EdificioRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AulaService {

    @Autowired
    private AulaRepository aulaRepository;

    @Autowired
    private EdificioRepository edificioRepository;
    public List<Aula> getAllAulas() {
        return aulaRepository.findAll();
    }

    public Optional<Aula> getAulaById(Long id) {
        return aulaRepository.findById(id);
    }

    public Aula saveOrUpdateAula(Aula aula) {
        return aulaRepository.save(aula);
    }

    public boolean deleteAula(Long id) {
        if (aulaRepository.existsById(id)) {  // Verifica si la aula existe
            aulaRepository.deleteById(id);  // Elimina la aula
            return true;  // Devuelve true si la aula fue eliminada
        } else {
            return false;  // Devuelve false si la aula no existía
        }
    }

    public Aula createOrUpdateAula(AulaDTO aulaDTO) {
        Edificio edificio = edificioRepository.findByNombre(aulaDTO.getNombreEdificio());
        if (edificio == null) {
            throw new RuntimeException("El edificio con nombre " + aulaDTO.getNombreEdificio() + " no existe.");
        }

        Aula aula = new Aula();
        aula.setCodigo(aulaDTO.getCodigo());
        aula.setDescripcion(aulaDTO.getDescripcion());
        aula.setCapacidad(aulaDTO.getCapacidad());
        aula.setEdificio(edificio); // Asocia el edificio encontrado por nombre

        return aulaRepository.save(aula);
    }
    public Optional<Aula> updateAula(Long id, AulaDTO aulaDTO) {
        // Verifica si la aula con el ID dado existe
        Optional<Aula> aulaExistente = aulaRepository.findById(id);
        if (aulaExistente.isPresent()) {
            Aula aula = aulaExistente.get();

            // Busca el edificio por nombre
            Edificio edificio = edificioRepository.findByNombre(aulaDTO.getNombreEdificio());
            if (edificio == null) {
                // Manejar el caso en que el edificio no exista, por ejemplo, lanzando una excepción
                throw new RuntimeException("Edificio con nombre " + aulaDTO.getNombreEdificio() + " no encontrado.");
            }

            // Actualiza los campos de la aula
            aula.setCodigo(aulaDTO.getCodigo());
            aula.setDescripcion(aulaDTO.getDescripcion());
            aula.setCapacidad(aulaDTO.getCapacidad());
            aula.setEdificio(edificio);

            // Guarda los cambios en la base de datos
            return Optional.of(aulaRepository.save(aula));
        } else {
            // Retorna un Optional vacío si la aula no existe
            return Optional.empty();
        }
    }
}

