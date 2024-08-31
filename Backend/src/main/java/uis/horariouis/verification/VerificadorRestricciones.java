package uis.horariouis.verification;

import org.springframework.stereotype.Service;
import uis.horariouis.model.*;

import java.sql.Time;
import java.util.List;

@Service
public class VerificadorRestricciones {

    public String verificarTodasLasRestricciones(List<Horario> horarios, List<Grupo> grupos) {
        StringBuilder errores = new StringBuilder();

        if (!verificarSolapamientoAulas(horarios, errores)) {
            errores.append("Error: Solapamiento de aulas encontrado.\n");
        }

        if (!verificarSolapamientoProfesores(horarios, errores)) {
            errores.append("Error: Solapamiento de profesores encontrado.\n");
        }

        if (!verificarHorasTeoria(horarios, grupos, errores)) {
            errores.append("Error: No se cumplieron las horas de teoría requeridas.\n");
        }

        if (!verificarDisponibilidadProfesores(horarios, errores)) {
            errores.append("Error: Un profesor no está disponible en uno o más horarios asignados.\n");
        }

        return errores.isEmpty() ? "Todas las restricciones se cumplieron correctamente." : errores.toString();
    }

    private boolean verificarSolapamientoAulas(List<Horario> horarios, StringBuilder errores) {
        for (int i = 0; i < horarios.size(); i++) {
            Horario h1 = horarios.get(i);
            for (int j = i + 1; j < horarios.size(); j++) {
                Horario h2 = horarios.get(j);
                if (h1.getAula().equals(h2.getAula()) &&
                        h1.getDia().equals(h2.getDia()) &&
                        timeOverlap(h1.getHoraInicio(), h1.getHoraFin(), h2.getHoraInicio(), h2.getHoraFin())) {
                    errores.append("Solapamiento de aulas: Aula ").append(h1.getAula().getCodigo())
                            .append(" en ").append(h1.getDia())
                            .append(" de ").append(h1.getHoraInicio())
                            .append(" a ").append(h1.getHoraFin()).append(" con otra clase.\n");
                    return false;
                }
            }
        }
        return true;
    }

    private boolean verificarSolapamientoProfesores(List<Horario> horarios, StringBuilder errores) {
        for (int i = 0; i < horarios.size(); i++) {
            Horario h1 = horarios.get(i);
            for (int j = i + 1; j < horarios.size(); j++) {
                Horario h2 = horarios.get(j);
                if (h1.getProfesor().equals(h2.getProfesor()) &&
                        h1.getDia().equals(h2.getDia()) &&
                        timeOverlap(h1.getHoraInicio(), h1.getHoraFin(), h2.getHoraInicio(), h2.getHoraFin())) {
                    errores.append("Solapamiento de profesores: Profesor ")
                            .append(h1.getProfesor().getNombreCompleto())
                            .append(" en ").append(h1.getDia())
                            .append(" de ").append(h1.getHoraInicio())
                            .append(" a ").append(h1.getHoraFin()).append(" con otra clase.\n");
                    return false;
                }
            }
        }
        return true;
    }

    private boolean verificarHorasTeoria(List<Horario> horarios, List<Grupo> grupos, StringBuilder errores) {
        boolean resultado = true;

        for (Grupo grupo : grupos) {
            int horasTeoriaAsignadas = 0;
            for (Horario horario : horarios) {
                if (horario.getGrupo().equals(grupo)) {
                    int duracion = calcularDuracionHoras(horario.getHoraInicio(), horario.getHoraFin());
                    horasTeoriaAsignadas += duracion;
                }
            }
            if (horasTeoriaAsignadas < grupo.getAsignatura().getHorasTeoria()) {
                errores.append("Horas de teoría insuficientes: Grupo ")
                        .append(grupo.getNombreGrupo()).append(" de la asignatura ")
                        .append(grupo.getAsignatura().getNombre())
                        .append(". Asignadas: ").append(horasTeoriaAsignadas)
                        .append(" horas, Requeridas: ")
                        .append(grupo.getAsignatura().getHorasTeoria()).append(" horas.\n");
                resultado = false;
            }
        }
        return resultado;
    }

    private boolean verificarDisponibilidadProfesores(List<Horario> horarios, StringBuilder errores) {
        boolean resultado = true;

        for (Horario horario : horarios) {
            Profesor profesor = horario.getProfesor();
            if (!profesorDisponible(profesor, horario.getDia(), horario.getHoraInicio(), horario.getHoraFin())) {
                errores.append("Disponibilidad de profesor: El profesor ")
                        .append(profesor.getNombreCompleto())
                        .append(" no está disponible el ").append(horario.getDia())
                        .append(" de ").append(horario.getHoraInicio())
                        .append(" a ").append(horario.getHoraFin()).append(".\n");
                resultado = false;
            }
        }
        return resultado;
    }

    private boolean timeOverlap(Time start1, Time end1, Time start2, Time end2) {
        return start1.before(end2) && start2.before(end1);
    }

    private int calcularDuracionHoras(Time horaInicio, Time horaFin) {
        long durationInMillis = horaFin.getTime() - horaInicio.getTime();
        return (int) (durationInMillis / (1000 * 60 * 60)); // Duración en horas
    }

    private boolean profesorDisponible(Profesor profesor, String dia, Time horaInicio, Time horaFin) {
        List<HorarioProfesor> horariosProfesor = profesor.getHorarioProfesores();

        for (HorarioProfesor horario : horariosProfesor) {
            if (horario.getDisponibilidadHoraria().getDia().equalsIgnoreCase(dia)) {
                Time horaInicioDisponibilidad = horario.getDisponibilidadHoraria().getHoraInicio();
                Time horaFinDisponibilidad = horario.getDisponibilidadHoraria().getHoraFin();

                if ((horaInicio.after(horaInicioDisponibilidad) || horaInicio.equals(horaInicioDisponibilidad)) &&
                        (horaFin.before(horaFinDisponibilidad) || horaFin.equals(horaFinDisponibilidad))) {
                    return true;
                }
            }
        }

        return false;
    }
}
