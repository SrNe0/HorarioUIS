package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.dto.DictadoDTO;
import uis.horariouis.exception.ResourceNotFoundException;
import uis.horariouis.model.Dictado;
import uis.horariouis.model.Profesor;
import uis.horariouis.model.Asignatura;
import uis.horariouis.repository.DictadoRepository;
import uis.horariouis.repository.ProfesorRepository;
import uis.horariouis.repository.AsignaturaRepository;

import java.util.List;
import java.util.Optional;

@Service
public class DictadoService {

    @Autowired
    private DictadoRepository dictadoRepository;

    @Autowired
    private ProfesorRepository profesorRepository;

    @Autowired
    private AsignaturaRepository asignaturaRepository;

    // Obtener todos los dictados
    public List<Dictado> getAllDictados() {
        return dictadoRepository.findAll();
    }

    // Obtener un dictado por ID
    public Optional<Dictado> getDictadoById(Long id) {
        return dictadoRepository.findById(id);
    }

    // Eliminar un dictado por ID
    public void deleteDictado(Long id) {
        dictadoRepository.deleteById(id);
    }

    // Crear un dictado a partir de un DictadoDTO
    public Dictado createDictadoFromDTO(DictadoDTO dictadoDTO) {
        // Buscar profesor por documentoIdentidad utilizando Optional
        Profesor profesor = profesorRepository.findByDocumentoIdentidad(dictadoDTO.getDocumentoIdentidadProfesor())
                .orElseThrow(() -> new ResourceNotFoundException("Profesor not found with documentoIdentidad: " + dictadoDTO.getDocumentoIdentidadProfesor()));

        // Buscar asignatura por código
        Asignatura asignatura = asignaturaRepository.findByCodigo(dictadoDTO.getCodigoAsignatura());
        if (asignatura == null) {
            throw new ResourceNotFoundException("Asignatura not found with code: " + dictadoDTO.getCodigoAsignatura());
        }

        // Crear el dictado
        Dictado dictado = new Dictado();
        dictado.setProfesor(profesor);
        dictado.setAsignatura(asignatura);
        return dictadoRepository.save(dictado);
    }

    // Actualizar un dictado existente
    public Dictado updateDictado(Long id, DictadoDTO dictadoDTO) {
        // Verificar si el dictado existe
        Dictado dictado = dictadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dictado not found with id: " + id));

        // Buscar el profesor por documentoIdentidad utilizando Optional
        Profesor profesor = profesorRepository.findByDocumentoIdentidad(dictadoDTO.getDocumentoIdentidadProfesor())
                .orElseThrow(() -> new ResourceNotFoundException("Profesor not found with documentoIdentidad: " + dictadoDTO.getDocumentoIdentidadProfesor()));

        // Buscar la asignatura por código
        Asignatura asignatura = asignaturaRepository.findByCodigo(dictadoDTO.getCodigoAsignatura());
        if (asignatura == null) {
            throw new ResourceNotFoundException("Asignatura not found with code: " + dictadoDTO.getCodigoAsignatura());
        }

        // Actualizar el dictado
        dictado.setProfesor(profesor);
        dictado.setAsignatura(asignatura);

        return dictadoRepository.save(dictado);
    }
}
