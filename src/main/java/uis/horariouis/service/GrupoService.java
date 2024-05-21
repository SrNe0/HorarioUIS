package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.dto.GrupoDTO;
import uis.horariouis.exception.ResourceNotFoundException;
import uis.horariouis.model.Asignatura;
import uis.horariouis.model.Grupo;
import uis.horariouis.repository.GrupoRepository;
import uis.horariouis.repository.AsignaturaRepository;

import java.util.List;
import java.util.Optional;

@Service
public class GrupoService {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private AsignaturaRepository asignaturaRepository;

    public List<Grupo> getAllGrupos() {
        return grupoRepository.findAll();
    }

    public Optional<Grupo> getGrupoById(Long id) {
        return grupoRepository.findById(id);
    }

    public void deleteGrupo(Long id) {
        grupoRepository.deleteById(id);
    }

    public Grupo createGrupoFromDTO(GrupoDTO grupoDTO) {
        Asignatura asignatura = asignaturaRepository.findByCodigo(grupoDTO.getCodigoAsignatura());
        if (asignatura == null) {
            throw new ResourceNotFoundException("Asignatura not found with code: " + grupoDTO.getCodigoAsignatura());
        }

        Grupo grupo = new Grupo();
        grupo.setAsignatura(asignatura);
        grupo.setNombreGrupo(grupoDTO.getNombre());
        grupo.setCupo(grupoDTO.getCupo());
        return grupoRepository.save(grupo);
    }

    public Grupo updateGrupo(Long id, GrupoDTO grupoDTO) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo not found with id: " + id));

        Asignatura asignatura = asignaturaRepository.findByCodigo(grupoDTO.getCodigoAsignatura());
        if (asignatura == null) {
            throw new ResourceNotFoundException("Asignatura not found with code: " + grupoDTO.getCodigoAsignatura());
        }

        grupo.setAsignatura(asignatura);
        grupo.setNombreGrupo(grupoDTO.getNombre());
        grupo.setCupo(grupoDTO.getCupo());

        return grupoRepository.save(grupo);
    }
}
