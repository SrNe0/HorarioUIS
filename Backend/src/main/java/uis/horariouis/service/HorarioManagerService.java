package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uis.horariouis.model.*;
import uis.horariouis.repository.*;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service // Marca la clase como un servicio de Spring
public class HorarioManagerService {

    @Autowired
    private DictadoRepository dictadoRepository; // Inyección del repositorio de Dictado

    @Autowired
    private GrupoRepository grupoRepository; // Inyección del repositorio de Grupo

    @Autowired
    private AulaRepository aulaRepository; // Inyección del repositorio de Aula

    @Autowired
    private HorarioRepository horarioRepository; // Inyección del repositorio de Horario

    @Autowired
    private HorarioProfesorRepository horarioProfesorRepository; // Inyección del repositorio de HorarioProfesor

    private static final Logger logger = Logger.getLogger(HorarioManagerService.class.getName()); // Logger para registrar mensajes

    @Transactional // Asegura que el método se ejecute dentro de una transacción
    public List<Horario> asignarHorarios() {
        // Obtiene todos los grupos y aulas disponibles
        List<Grupo> grupos = grupoRepository.findAll();
        List<Aula> aulas = aulaRepository.findAll();
        List<Horario> horarios = new ArrayList<>();

        // Itera sobre cada grupo para asignarles horarios
        for (Grupo grupo : grupos) {
            try {
                // Intenta asignar horarios al grupo
                asignarHorarioAGrupo(grupo, aulas, horarios);
            } catch (HorarioException e) {
                // Si hay una excepción, la registra en el logger
                logger.warning(e.getMessage());
            }
        }

        // Guarda todos los horarios asignados en el repositorio
        horarioRepository.saveAll(horarios);
        return horarios;
    }

    // Método para asignar horarios a un grupo específico
    private void asignarHorarioAGrupo(Grupo grupo, List<Aula> aulas, List<Horario> horarios) throws HorarioException {
        // Obtiene los dictados para la asignatura del grupo
        List<Dictado> dictados = dictadoRepository.findByAsignatura(grupo.getAsignatura());
        if (dictados == null || dictados.isEmpty()) {
            // Si no hay dictados, lanza una excepción
            throw new HorarioException("No se encontró dictado para la asignatura " + grupo.getAsignatura().getNombre());
        } else {
            // Si hay dictados, toma el primero
            Dictado dictado = dictados.get(0);
            Profesor profesor = dictado.getProfesor();
            // Obtiene la disponibilidad horaria del profesor
            List<DisponibilidadHoraria> disponibilidad = obtenerDisponibilidadProfesor(profesor);

            //[WARNING] - PROBLEMA DE DEFINICION DE LAS REGLAS DE JUEGO PARA LA VARIABLE HORAS TOTALES

            //Las horas totales no se sabe si se asignan como una suma de horas teoricas y practicas, o si se dan en aulas diferentes
            //y si se debe especificar un salon especial para ciertas asignaturas
            // Calcula las horas totales a asignar (teoría + práctica)
            int horasTotales = grupo.getAsignatura().getHorasTeoria() + grupo.getAsignatura().getHorasPractica();
            // Divide las horas totales en dos bloques
            int horasBloque1 = horasTotales / 2;
            int horasBloque2 = horasTotales - horasBloque1;

            // Intenta asignar el primer bloque de horas
            if (asignarBloqueDeHoras(grupo, aulas, horarios, profesor, disponibilidad, horasBloque1)) {
                throw new HorarioException("No se pudo asignar el primer bloque de horas para el grupo " + grupo.getNombreGrupo() + " de la asignatura " + grupo.getAsignatura().getNombre());
            }

            // Intenta asignar el segundo bloque de horas
            if (asignarBloqueDeHoras(grupo, aulas, horarios, profesor, disponibilidad, horasBloque2)) {
                throw new HorarioException("No se pudo asignar el segundo bloque de horas para el grupo " + grupo.getNombreGrupo() + " de la asignatura " + grupo.getAsignatura().getNombre());
            }
        }
    }

    // Método para asignar un bloque de horas a un grupo
    private boolean asignarBloqueDeHoras(Grupo grupo, List<Aula> aulas, List<Horario> horarios, Profesor profesor, List<DisponibilidadHoraria> disponibilidad, int horas) {
        // Lista de días de la semana
        List<String> diasSemana = Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado");

        // Itera sobre cada día de la semana
        for (String dia : diasSemana) {
            // Filtra la disponibilidad horaria del profesor por día
            List<DisponibilidadHoraria> disponibilidadDia = disponibilidad.stream()
                    .filter(slot -> slot.getDia().equals(dia))
                    .collect(Collectors.toList());

            // Obtiene slots consecutivos de disponibilidad que cumplan con las horas necesarias
            List<DisponibilidadHoraria> slotsConsecutivos = obtenerSlotsConsecutivos(disponibilidadDia, horas);

            if (!slotsConsecutivos.isEmpty()) {
                // Verifica si hay un aula disponible
                Aula aulaDisponible = obtenerAulaDisponible(aulas, horarios, grupo, slotsConsecutivos);

                if (aulaDisponible != null) {
                    // Si hay un aula disponible, crea horarios para cada slot y los agrega a la lista de horarios
                    for (DisponibilidadHoraria slot : slotsConsecutivos) {
                        Horario horario = crearHorario(profesor, grupo, aulaDisponible, slot);
                        horarios.add(horario);
                        disponibilidad.remove(slot); // Remueve también de la disponibilidad original
                    }
                    return false; // Retorna true si se pudo asignar el bloque de horas
                }
            }
        }

        return true; // Retorna false si no se pudo asignar el bloque de horas
    }

    // Método para crear un objeto Horario
    private Horario crearHorario(Profesor profesor, Grupo grupo, Aula aula, DisponibilidadHoraria slot) {
        Horario horario = new Horario();
        horario.setProfesor(profesor);
        horario.setGrupo(grupo);
        horario.setAula(aula);
        horario.setDia(slot.getDia());
        horario.setHoraInicio(slot.getHoraInicio());
        horario.setHoraFin(slot.getHoraFin());
        return horario; // Retorna el objeto Horario creado
    }

    // Método para obtener la disponibilidad horaria de un profesor
    private List<DisponibilidadHoraria> obtenerDisponibilidadProfesor(Profesor profesor) {
        // Obtiene los horarios del profesor
        List<HorarioProfesor> horariosProfesor = horarioProfesorRepository.findByProfesor(profesor);
        // Convierte los horarios a disponibilidad horaria y los ordena por día y hora de inicio
        return horariosProfesor.stream()
                .map(HorarioProfesor::getDisponibilidadHoraria)
                .sorted(Comparator.comparing(DisponibilidadHoraria::getDia)
                        .thenComparing(DisponibilidadHoraria::getHoraInicio))
                .collect(Collectors.toList());
    }

    // Método para obtener slots consecutivos de disponibilidad
    private List<DisponibilidadHoraria> obtenerSlotsConsecutivos(List<DisponibilidadHoraria> disponibilidad, int horasNecesarias) {
        List<DisponibilidadHoraria> slotsConsecutivos = new ArrayList<>();
        int horasConsecutivas = 0;

        // Itera sobre la disponibilidad horaria
        for (int i = 0; i < disponibilidad.size(); i++) {
            if (horasConsecutivas == horasNecesarias) {
                break; // Si se alcanzan las horas necesarias, se detiene el bucle
            }

            DisponibilidadHoraria actual = disponibilidad.get(i);
            slotsConsecutivos.add(actual); // Agrega el slot actual a los slots consecutivos
            horasConsecutivas++;

            if (i < disponibilidad.size() - 1) {
                DisponibilidadHoraria siguiente = disponibilidad.get(i + 1);
                // Verifica si el siguiente slot es consecutivo
                if (!actual.getDia().equals(siguiente.getDia()) || !actual.getHoraFin().equals(siguiente.getHoraInicio())) {
                    slotsConsecutivos.clear(); // Si no es consecutivo, se reinicia la lista
                    horasConsecutivas = 0;
                }
            }
        }

        // Retorna los slots consecutivos si se alcanzan las horas necesarias, de lo contrario, una lista vacía
        return horasConsecutivas == horasNecesarias ? slotsConsecutivos : new ArrayList<>();
    }

    // Método para obtener una aula disponible
    private Aula obtenerAulaDisponible(List<Aula> aulas, List<Horario> horarios, Grupo grupo, List<DisponibilidadHoraria> slotsConsecutivos) {
        for (Aula aula : aulas) {
            boolean isAulaDisponible = true;
            // Verifica si el aula está disponible para cada slot consecutivo
            for (DisponibilidadHoraria slot : slotsConsecutivos) {
                if (horarios.stream().anyMatch(horario ->
                        horario.getAula().equals(aula) &&
                                horario.getDia().equals(slot.getDia()) &&
                                horario.getHoraInicio().equals(slot.getHoraInicio())
                )) {
                    isAulaDisponible = false; // Si el aula no está disponible, se marca como false
                    break;
                }
            }
            // Si el aula está disponible y tiene capacidad suficiente, se retorna
            if (isAulaDisponible && aula.getCapacidad() >= grupo.getCupo()) {
                return aula;
            }
        }
        return null; // Retorna null si no se encuentra un aula disponible
    }

    // Excepción personalizada para manejar errores de asignación de horarios
    public static class HorarioException extends Exception {
        public HorarioException(String message) {
            super(message); // Llama al constructor de la superclase Exception con el mensaje proporcionado
        }
    }
}
