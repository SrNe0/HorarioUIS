package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uis.horariouis.model.*;
import uis.horariouis.repository.*;

import java.util.*;
import java.util.logging.Logger;
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

    private static final Logger logger = Logger.getLogger(HorarioManagerService.class.getName());

    @Transactional
    public List<Horario> asignarHorarios() {
        List<Grupo> grupos = grupoRepository.findAll();
        List<Aula> aulas = aulaRepository.findAll();
        List<Horario> horarios = new ArrayList<>();

        for (Grupo grupo : grupos) {
            try {
                asignarHorarioAGrupo(grupo, aulas, horarios);
            } catch (HorarioException e) {
                logger.warning(e.getMessage());
            }
        }

        horarioRepository.saveAll(horarios);
        return horarios;
    }

    private void asignarHorarioAGrupo(Grupo grupo, List<Aula> aulas, List<Horario> horarios) throws HorarioException {
        List<Dictado> dictados = dictadoRepository.findByAsignatura(grupo.getAsignatura());
        if (dictados == null || dictados.isEmpty()) {
            throw new HorarioException("No se encontró dictado para la asignatura " + grupo.getAsignatura().getNombre());
        }

        Dictado dictado = dictados.get(0); // Tomamos el primer dictado si hay múltiples
        Profesor profesor = dictado.getProfesor();
        List<DisponibilidadHoraria> disponibilidad = obtenerDisponibilidadProfesor(profesor);

        int horasTotales = grupo.getAsignatura().getHorasTeoria() + grupo.getAsignatura().getHorasPractica();

        // Dividir horas en dos bloques, sumando 1 hora al segundo bloque si es impar
        int horasBloque1 = horasTotales / 2;
        int horasBloque2 = horasTotales - horasBloque1;

        if (!asignarBloqueDeHoras(grupo, aulas, horarios, profesor, disponibilidad, horasBloque1)) {
            throw new HorarioException("No se pudo asignar el primer bloque de horas para el grupo " + grupo.getNombreGrupo() + " de la asignatura " + grupo.getAsignatura().getNombre());
        }

        if (!asignarBloqueDeHoras(grupo, aulas, horarios, profesor, disponibilidad, horasBloque2)) {
            throw new HorarioException("No se pudo asignar el segundo bloque de horas para el grupo " + grupo.getNombreGrupo() + " de la asignatura " + grupo.getAsignatura().getNombre());
        }
    }

    private boolean asignarBloqueDeHoras(Grupo grupo, List<Aula> aulas, List<Horario> horarios, Profesor profesor, List<DisponibilidadHoraria> disponibilidad, int horas) {
        List<String> diasSemana = Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado");

        for (String dia : diasSemana) {
            List<DisponibilidadHoraria> disponibilidadDia = disponibilidad.stream()
                    .filter(slot -> slot.getDia().equals(dia))
                    .collect(Collectors.toList());

            List<DisponibilidadHoraria> slotsConsecutivos = obtenerSlotsConsecutivos(disponibilidadDia, horas);

            if (!slotsConsecutivos.isEmpty()) {
                Aula aulaDisponible = obtenerAulaDisponible(aulas, horarios, grupo, slotsConsecutivos);

                if (aulaDisponible != null) {
                    for (DisponibilidadHoraria slot : slotsConsecutivos) {
                        Horario horario = crearHorario(profesor, grupo, aulaDisponible, slot);
                        horarios.add(horario);

                        disponibilidad.remove(slot); // Remover también de la disponibilidad original
                    }
                    return true;
                }
            }
        }

        return false;
    }

    private Horario crearHorario(Profesor profesor, Grupo grupo, Aula aula, DisponibilidadHoraria slot) {
        Horario horario = new Horario();
        horario.setProfesor(profesor);
        horario.setGrupo(grupo);
        horario.setAula(aula);
        horario.setDia(slot.getDia());
        horario.setHoraInicio(slot.getHoraInicio());
        horario.setHoraFin(slot.getHoraFin());
        return horario;
    }

    private List<DisponibilidadHoraria> obtenerDisponibilidadProfesor(Profesor profesor) {
        List<HorarioProfesor> horariosProfesor = horarioProfesorRepository.findByProfesor(profesor);
        return horariosProfesor.stream()
                .map(HorarioProfesor::getDisponibilidadHoraria)
                .sorted(Comparator.comparing(DisponibilidadHoraria::getDia)
                        .thenComparing(DisponibilidadHoraria::getHoraInicio))
                .collect(Collectors.toList());
    }

    private List<DisponibilidadHoraria> obtenerSlotsConsecutivos(List<DisponibilidadHoraria> disponibilidad, int horasNecesarias) {
        List<DisponibilidadHoraria> slotsConsecutivos = new ArrayList<>();
        int horasConsecutivas = 0;

        for (int i = 0; i < disponibilidad.size(); i++) {
            if (horasConsecutivas == horasNecesarias) {
                break;
            }

            DisponibilidadHoraria actual = disponibilidad.get(i);
            slotsConsecutivos.add(actual);
            horasConsecutivas++;

            if (i < disponibilidad.size() - 1) {
                DisponibilidadHoraria siguiente = disponibilidad.get(i + 1);
                if (!actual.getDia().equals(siguiente.getDia()) || !actual.getHoraFin().equals(siguiente.getHoraInicio())) {
                    slotsConsecutivos.clear();
                    horasConsecutivas = 0;
                }
            }
        }

        return horasConsecutivas == horasNecesarias ? slotsConsecutivos : new ArrayList<>();
    }

    private Aula obtenerAulaDisponible(List<Aula> aulas, List<Horario> horarios, Grupo grupo, List<DisponibilidadHoraria> slotsConsecutivos) {
        for (Aula aula : aulas) {
            boolean isAulaDisponible = true;
            for (DisponibilidadHoraria slot : slotsConsecutivos) {
                if (horarios.stream().anyMatch(horario ->
                        horario.getAula().equals(aula) &&
                                horario.getDia().equals(slot.getDia()) &&
                                horario.getHoraInicio().equals(slot.getHoraInicio())
                )) {
                    isAulaDisponible = false;
                    break;
                }
            }
            if (isAulaDisponible && aula.getCapacidad() >= grupo.getCupo()) {
                return aula;
            }
        }
        return null;
    }

    // Custom exception for handling scheduling errors
    public static class HorarioException extends Exception {
        public HorarioException(String message) {
            super(message);
        }
    }
}
