package uis.horariouis.verification;

import org.springframework.stereotype.Service;
import uis.horariouis.model.*;

import java.sql.Time;
import java.util.List;

@Service // Marca la clase como un servicio de Spring para que pueda ser inyectada en otros componentes.
public class VerificadorRestricciones {

    // Método principal que verifica todas las restricciones sobre los horarios.
    public String verificarTodasLasRestricciones(List<Horario> horarios, List<Grupo> grupos) {
        StringBuilder errores = new StringBuilder(); // Para acumular errores encontrados durante las verificaciones.

        // Verifica si hay solapamientos de aulas en los horarios.
        if (!verificarSolapamientoAulas(horarios, errores)) {
            errores.append("Error: Solapamiento de aulas encontrado.\n"); // Agrega un mensaje de error si se encuentra un solapamiento de aulas.
        }

        // Verifica si hay solapamientos de profesores en los horarios.
        if (!verificarSolapamientoProfesores(horarios, errores)) {
            errores.append("Error: Solapamiento de profesores encontrado.\n"); // Agrega un mensaje de error si se encuentra un solapamiento de profesores.
        }

        // Verifica si se cumplen las horas de teoría requeridas para cada grupo.
        if (!verificarHorasTeoria(horarios, grupos, errores)) {
            errores.append("Error: No se cumplieron las horas de teoría requeridas.\n"); // Agrega un mensaje de error si no se cumplen las horas de teoría.
        }

        // Verifica si los profesores están disponibles en los horarios asignados.
        if (!verificarDisponibilidadProfesores(horarios, errores)) {
            errores.append("Error: Un profesor no está disponible en uno o más horarios asignados.\n"); // Agrega un mensaje de error si algún profesor no está disponible.
        }

        // Retorna un mensaje indicando si todas las restricciones se cumplieron o si hubo errores.
        return errores.isEmpty() ? "Todas las restricciones se cumplieron correctamente." : errores.toString();
    }

    // Verifica si hay solapamientos de aulas en la lista de horarios.
    private boolean verificarSolapamientoAulas(List<Horario> horarios, StringBuilder errores) {
        for (int i = 0; i < horarios.size(); i++) {
            Horario h1 = horarios.get(i);
            for (int j = i + 1; j < horarios.size(); j++) {
                Horario h2 = horarios.get(j);
                // Compara si dos horarios utilizan la misma aula en el mismo día y si sus horarios se solapan.
                if (h1.getAula().equals(h2.getAula()) &&
                        h1.getDia().equals(h2.getDia()) &&
                        timeOverlap(h1.getHoraInicio(), h1.getHoraFin(), h2.getHoraInicio(), h2.getHoraFin())) {
                    // Si hay solapamiento, agrega un mensaje detallado de error.
                    errores.append("Solapamiento de aulas: Aula ").append(h1.getAula().getCodigo())
                            .append(" en ").append(h1.getDia())
                            .append(" de ").append(h1.getHoraInicio())
                            .append(" a ").append(h1.getHoraFin()).append(" con otra clase.\n");
                    return false; // Retorna, false indicando que se encontró un solapamiento.
                }
            }
        }
        return true; // Retorna true si no se encuentran solapamientos de aulas.
    }

    // Verifica si hay solapamientos de profesores en la lista de horarios.
    private boolean verificarSolapamientoProfesores(List<Horario> horarios, StringBuilder errores) {
        for (int i = 0; i < horarios.size(); i++) {
            Horario h1 = horarios.get(i);
            for (int j = i + 1; j < horarios.size(); j++) {
                Horario h2 = horarios.get(j);
                // Compara si dos horarios utilizan el mismo profesor en el mismo día y si sus horarios se solapan.
                if (h1.getProfesor().equals(h2.getProfesor()) &&
                        h1.getDia().equals(h2.getDia()) &&
                        timeOverlap(h1.getHoraInicio(), h1.getHoraFin(), h2.getHoraInicio(), h2.getHoraFin())) {
                    // Si hay solapamiento, agrega un mensaje detallado de error.
                    errores.append("Solapamiento de profesores: Profesor ")
                            .append(h1.getProfesor().getNombreCompleto())
                            .append(" en ").append(h1.getDia())
                            .append(" de ").append(h1.getHoraInicio())
                            .append(" a ").append(h1.getHoraFin()).append(" con otra clase.\n");
                    return false; // Retorna, false indicando que se encontró un solapamiento.
                }
            }
        }
        return true; // Retorna true si no se encuentran solapamientos de profesores.
    }

    // Verifica si se cumplen las horas de teoría requeridas para cada grupo.
    private boolean verificarHorasTeoria(List<Horario> horarios, List<Grupo> grupos, StringBuilder errores) {
        boolean resultado = true;

        for (Grupo grupo : grupos) {
            int horasTeoriaAsignadas = 0; // Inicializa el conteo de horas de teoría asignadas para el grupo.
            for (Horario horario : horarios) {
                if (horario.getGrupo().equals(grupo)) {
                    // Calcula la duración de cada horario y la suma al total de horas asignadas.
                    int duracion = calcularDuracionHoras(horario.getHoraInicio(), horario.getHoraFin());
                    horasTeoriaAsignadas += duracion;
                }
            }
            // Compara las horas asignadas con las horas requeridas por la asignatura.
            if (horasTeoriaAsignadas < grupo.getAsignatura().getHorasTeoria()) {
                // Si no se cumplen las horas requeridas, agrega un mensaje detallado de error.
                errores.append("Horas de teoría insuficientes: Grupo ")
                        .append(grupo.getNombreGrupo()).append(" de la asignatura ")
                        .append(grupo.getAsignatura().getNombre())
                        .append(". Asignadas: ").append(horasTeoriaAsignadas)
                        .append(" horas, Requeridas: ")
                        .append(grupo.getAsignatura().getHorasTeoria()).append(" horas.\n");
                resultado = false; // Marca el resultado como falso.
            }
        }
        return resultado; // Retorna true si se cumplen todas las horas requeridas, false en caso contrario.
    }

    // Verifica si los profesores están disponibles en los horarios asignados.
    private boolean verificarDisponibilidadProfesores(List<Horario> horarios, StringBuilder errores) {
        boolean resultado = true;

        for (Horario horario : horarios) {
            Profesor profesor = horario.getProfesor();
            // Verifica si el profesor está disponible en el horario asignado.
            if (!profesorDisponible(profesor, horario.getDia(), horario.getHoraInicio(), horario.getHoraFin())) {
                // Si el profesor no está disponible, agrega un mensaje detallado de error.
                errores.append("Disponibilidad de profesor: El profesor ")
                        .append(profesor.getNombreCompleto())
                        .append(" no está disponible el ").append(horario.getDia())
                        .append(" de ").append(horario.getHoraInicio())
                        .append(" a ").append(horario.getHoraFin()).append(".\n");
                resultado = false; // Marca el resultado como falso.
            }
        }
        return resultado; // Retorna true si todos los profesores están disponibles, false en caso contrario.
    }

    // Método auxiliar que verifica si dos intervalos de tiempo se solapan.
    private boolean timeOverlap(Time start1, Time end1, Time start2, Time end2) {
        // Retorna true si el tiempo de inicio del primer intervalo es antes del final del segundo y viceversa.
        return start1.before(end2) && start2.before(end1);
    }

    // Calcula la duración en horas entre dos instantes de tiempo.
    private int calcularDuracionHoras(Time horaInicio, Time horaFin) {
        long durationInMillis = horaFin.getTime() - horaInicio.getTime(); // Calcula la duración en milisegundos.
        return (int) (durationInMillis / (1000 * 60 * 60)); // Convierte la duración a horas y la retorna.
    }

    // Verifica si un profesor está disponible en un día y horario específico.
    private boolean profesorDisponible(Profesor profesor, String dia, Time horaInicio, Time horaFin) {
        List<HorarioProfesor> horariosProfesor = profesor.getHorarioProfesores();

        for (HorarioProfesor horario : horariosProfesor) {
            // Verifica si el día del horario coincide con el día en cuestión.
            if (horario.getDisponibilidadHoraria().getDia().equalsIgnoreCase(dia)) {
                Time horaInicioDisponibilidad = horario.getDisponibilidadHoraria().getHoraInicio();
                Time horaFinDisponibilidad = horario.getDisponibilidadHoraria().getHoraFin();

                // Verifica si el horario en cuestión está completamente dentro de la disponibilidad del profesor.
                if ((horaInicio.after(horaInicioDisponibilidad) || horaInicio.equals(horaInicioDisponibilidad)) &&
                        (horaFin.before(horaFinDisponibilidad) || horaFin.equals(horaFinDisponibilidad))) {
                    return true; // Retorna true si el profesor está disponible.
                }
            }
        }

        return false; // Retorna, false si el profesor no está disponible.
    }
}
