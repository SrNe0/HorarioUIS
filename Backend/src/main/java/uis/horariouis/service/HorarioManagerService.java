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
        List<String> razonesNoAsignacion = new ArrayList<>();

        for (Grupo grupo : grupos) {
            try {
                asignarHorarioAGrupo(grupo, aulas, horarios);
            } catch (HorarioException e) {
                logger.warning(e.getMessage());
                razonesNoAsignacion.add(e.getMessage());
            }
        }

        horarioRepository.saveAll(horarios);

        if (!razonesNoAsignacion.isEmpty()) {
            System.out.println("Razones por las que no se asignaron horarios a algunos grupos:");
            for (String razon : razonesNoAsignacion) {
                System.out.println(razon);
            }
        }

        return horarios;
    }

    private void asignarHorarioAGrupo(Grupo grupo, List<Aula> aulas, List<Horario> horarios) throws HorarioException {
        List<Dictado> dictados = dictadoRepository.findByAsignatura(grupo.getAsignatura());
        if (dictados == null || dictados.isEmpty()) {
            throw new HorarioException("No se encontró dictado para la asignatura " + grupo.getAsignatura().getNombre());
        }

        boolean asignado = false;
        int horasRestantes = grupo.getAsignatura().getHorasTeoria() + grupo.getAsignatura().getHorasPractica();

        for (Dictado dictado : dictados) {
            Profesor profesor = dictado.getProfesor();
            List<DisponibilidadHoraria> disponibilidad = obtenerDisponibilidadProfesor(profesor);

            if (!verificarDisponibilidadProfesor(profesor, disponibilidad, horasRestantes)) {
                continue; // Intenta con el siguiente dictado
            }

            boolean asignarBloqueDe3Horas = horasRestantes % 2 == 1;

            while (horasRestantes > 0) {
                int horasABloquear = asignarBloqueDe3Horas ? 3 : 2;
                if (asignarBloqueDeHoras(grupo, aulas, horarios, profesor, disponibilidad, horasABloquear, horasRestantes)) {
                    horasRestantes -= horasABloquear;
                } else {
                    if (asignarBloqueDe3Horas) {
                        horasABloquear = 2;
                        if (asignarBloqueDeHoras(grupo, aulas, horarios, profesor, disponibilidad, horasABloquear, horasRestantes)) {
                            horasRestantes -= horasABloquear;
                        } else {
                            break; // No se pudo asignar el bloque, intenta con el siguiente dictado
                        }
                    } else {
                        break; // No se pudo asignar el bloque, intenta con el siguiente dictado
                    }
                }
            }

            if (horasRestantes == 0) {
                asignado = true;
                break; // Se asignaron todas las horas, salir del bucle
            }
        }

        if (!asignado) {
            throw new HorarioException("No se pudo asignar un horario completo para el grupo " + grupo.getNombreGrupo() + " de la asignatura " + grupo.getAsignatura().getNombre());
        }
    }

    private boolean asignarBloqueDeHoras(Grupo grupo, List<Aula> aulas, List<Horario> horarios, Profesor profesor, List<DisponibilidadHoraria> disponibilidad, int horas, int horasRestantes) {
        List<String> diasSemana = Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado");
        Set<String> diasOcupados = horarios.stream()
                .filter(h -> h.getGrupo().equals(grupo))
                .map(Horario::getDia)
                .collect(Collectors.toSet());

        for (String dia : diasSemana) {
            if (diasOcupados.contains(dia)) continue;

            List<DisponibilidadHoraria> disponibilidadDia = disponibilidad.stream()
                    .filter(slot -> slot.getDia().equals(dia))
                    .collect(Collectors.toList());

            List<DisponibilidadHoraria> slotsConsecutivos = obtenerSlotsConsecutivos(disponibilidadDia, horas);

            if (!slotsConsecutivos.isEmpty()) {
                Aula aulaDisponible = obtenerAulaDisponible(aulas, horarios, grupo, slotsConsecutivos);

                if (aulaDisponible != null) {
                    if (!verificarHorasDiarias(grupo, horarios, dia, horas, horasRestantes)) {
                        logger.warning("El grupo " + grupo.getNombreGrupo() + " excede las horas diarias permitidas en el día " + dia);
                        continue;
                    }

                    for (DisponibilidadHoraria slot : slotsConsecutivos) {
                        if (verificarSolapamientoProfesor(profesor, horarios, slot)) {
                            Horario horario = crearHorario(profesor, grupo, aulaDisponible, slot);
                            horarios.add(horario);
                            disponibilidad.remove(slot);
                        } else {
                            logger.warning("Solapamiento de horarios para el profesor " + profesor.getNombreCompleto() + " en el día " + slot.getDia() + " de " + slot.getHoraInicio() + " a " + slot.getHoraFin());
                            return false;
                        }
                    }
                    diasOcupados.add(dia);
                    return true;
                } else {
                    logger.warning("No hay aula disponible para el grupo " + grupo.getNombreGrupo() + " en el día " + dia);
                }
            } else {
                logger.warning("No hay disponibilidad suficiente para asignar " + horas + " horas consecutivas para el grupo " + grupo.getNombreGrupo() + " en el día " + dia);
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

    private boolean verificarDisponibilidadProfesor(Profesor profesor, List<DisponibilidadHoraria> disponibilidad, int horasNecesarias) {
        int horasDisponibles = disponibilidad.stream()
                .mapToInt(slot -> calcularDuracion(slot.getHoraInicio(), slot.getHoraFin()))
                .sum();
        return horasDisponibles >= horasNecesarias;
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

            if (isAulaDisponible && (grupo.getAsignatura().getNecesitaComputadores() == aula.getTieneComputadores())) {
                return aula;
            }
        }
        return null;
    }

    private boolean verificarHorasDiarias(Grupo grupo, List<Horario> horarios, String dia, int horas, int horasRestantes) {
        int horasDiarias = horarios.stream()
                .filter(horario -> horario.getGrupo().equals(grupo) && horario.getDia().equals(dia))
                .mapToInt(horario -> calcularDuracion(horario.getHoraInicio(), horario.getHoraFin()))
                .sum();
        return (horasDiarias + horas) <= (horasRestantes % 2 == 1 ? 3 : 2);
    }

    private int calcularDuracion(Date horaInicio, Date horaFin) {
        long diff = horaFin.getTime() - horaInicio.getTime();
        return (int) (diff / (1000 * 60 * 60));
    }

    private boolean verificarSolapamientoProfesor(Profesor profesor, List<Horario> horarios, DisponibilidadHoraria slot) {
        return horarios.stream().noneMatch(horario ->
                horario.getProfesor().equals(profesor) &&
                        horario.getDia().equals(slot.getDia()) &&
                        horario.getHoraInicio().equals(slot.getHoraInicio())
        );
    }

    public static class HorarioException extends Exception {
        public HorarioException(String message) {
            super(message);
        }
    }
}
