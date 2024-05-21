package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.model.*;
import uis.horariouis.repository.AulaRepository;
import uis.horariouis.repository.DictadoRepository;
import uis.horariouis.repository.GrupoRepository;
import uis.horariouis.repository.HorarioProfesorRepository;
import uis.horariouis.repository.HorarioRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HorarioManagerService {

    @Autowired
    private DictadoRepository dictadoRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private AulaRepository aulaRepository;

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private HorarioProfesorRepository horarioProfesorRepository;

    public List<Horario> asignarHorarios() {
        List<Dictado> dictados = dictadoRepository.findAll();
        List<Grupo> grupos = grupoRepository.findAll();
        List<Aula> aulas = aulaRepository.findAll();
        List<Horario> horarios = new ArrayList<>();

        for (Dictado dictado : dictados) {
            Profesor profesor = dictado.getProfesor();
            List<DisponibilidadHoraria> disponibilidad = obtenerDisponibilidadProfesor(profesor);

            Grupo grupo = obtenerGrupoAsignatura(dictado.getAsignatura(), grupos);
            if (grupo == null) {
                System.out.println("No se encontró grupo para la asignatura " + dictado.getAsignatura().getNombre());
                continue;
            }

            int horasTotales = dictado.getAsignatura().getHorasTeoria() + dictado.getAsignatura().getHorasPractica();

            asignarHoras(horarios, disponibilidad, aulas, profesor, grupo, horasTotales);
        }

        horarioRepository.saveAll(horarios);
        return horarios;
    }

    private void asignarHoras(List<Horario> horarios, List<DisponibilidadHoraria> disponibilidad, List<Aula> aulas,
                              Profesor profesor, Grupo grupo, int horas) {
        for (int i = 0; i < horas; i++) {
            Optional<DisponibilidadHoraria> slotDisponible = obtenerSlotDisponible(disponibilidad);
            Optional<Aula> aulaDisponible = obtenerAulaDisponible(aulas, grupo);

            if (slotDisponible.isPresent() && aulaDisponible.isPresent()) {
                Horario horario = new Horario();
                horario.setProfesor(profesor);
                horario.setGrupo(grupo);
                horario.setAula(aulaDisponible.get());
                horario.setDia(slotDisponible.get().getDia());
                horario.setHoraInicio(slotDisponible.get().getHoraInicio());
                horario.setHoraFin(slotDisponible.get().getHoraFin());
                horarios.add(horario);

                disponibilidad.remove(slotDisponible.get());
            } else {
                System.out.println("No se pudo asignar hora para " + grupo.getAsignatura().getNombre() + " con el profesor " + profesor.getNombre1() + " " + profesor.getApellido1());
            }
        }
    }

    private List<DisponibilidadHoraria> obtenerDisponibilidadProfesor(Profesor profesor) {
        List<HorarioProfesor> horariosProfesor = horarioProfesorRepository.findByProfesor(profesor);
        return horariosProfesor.stream()
                .map(HorarioProfesor::getDisponibilidadHoraria)
                .collect(Collectors.toList());
    }

    private Grupo obtenerGrupoAsignatura(Asignatura asignatura, List<Grupo> grupos) {
        for (Grupo grupo : grupos) {
            if (grupo.getAsignatura().getIdAsignatura().equals(asignatura.getIdAsignatura())) {
                return grupo;
            }
        }
        return null;
    }

    private Optional<DisponibilidadHoraria> obtenerSlotDisponible(List<DisponibilidadHoraria> disponibilidad) {
        if (!disponibilidad.isEmpty()) {
            return Optional.of(disponibilidad.get(0));
        }
        return Optional.empty();
    }

    private Optional<Aula> obtenerAulaDisponible(List<Aula> aulas, Grupo grupo) {
        for (Aula aula : aulas) {
            if (aula.getCapacidad() >= grupo.getCupo()) {
                return Optional.of(aula);
            }
        }
        return Optional.empty();
    }
}
