package uis.horariouis.verification;

import org.springframework.stereotype.Service;
import uis.horariouis.model.*;

import java.sql.Time;
import java.util.List;

@Service // Anotación que indica que esta clase es un servicio de Spring y puede ser inyectada en otros componentes.
public class RestriccionesAlgoritmoGenetico {

//    Verificar diferentes restricciones de un cromosoma (representando una solución de horarios)
//    y calcular su aptitud, que es una medida de cuán buena es la solución.

    // Método que calcula la aptitud de un cromosoma en función de varias restricciones.
    public int calcularAptitud(Cromosoma cromosoma) {
        int aptitud = 0;

        // Verifica si no hay solapamiento de profesor y ajusta la aptitud en consecuencia.
        if (!haySolapamientoProfesor(cromosoma)) {
            aptitud += 20;
        } else {
            aptitud -= 50;
        }

        // Verifica si no hay solapamiento de aula y ajusta la aptitud en consecuencia.
        if (!haySolapamientoAula(cromosoma)) {
            aptitud += 20;
        } else {
            aptitud -= 50;
        }

        // Verifica si se cumplen las horas de teoría necesarias y ajusta la aptitud en consecuencia.
        if (cumpleHorasTeoria(cromosoma)) {
            aptitud += 20;
        } else {
            aptitud -= 30;
        }

        // Verifica si se asignaron las aulas correctas y ajusta la aptitud en consecuencia.
        if (aulasCorrectasAsignadas(cromosoma)) {
            aptitud += 20;
        } else {
            aptitud -= 40;
        }

        // Verifica si la disponibilidad del profesor se cumple y ajusta la aptitud en consecuencia.
        if (disponibilidadProfesorCumplida(cromosoma)) {
            aptitud += 20;
        } else {
            aptitud -= 40;
        }

        // Verifica si el profesor dicta la asignatura del grupo y ajusta la aptitud en consecuencia.
        if (profesorDictaAsignaturaDelGrupo(cromosoma)) {
            aptitud += 20;
        } else {
            aptitud -= 50;
        }

        return aptitud; // Retorna el valor final de aptitud calculado.
    }

    // Método que verifica si el profesor del gen dicta la asignatura del grupo.
    public boolean profesorDictaAsignaturaDelGrupo(Cromosoma cromosoma) {
        for (Gen gen : cromosoma.getGenes()) {
            Profesor profesor = gen.getProfesor();
            Asignatura asignaturaGrupo = gen.getGrupo().getAsignatura();

            // Verifica si el profesor dicta la asignatura del grupo.
            boolean dictaAsignatura = profesor.getDictados().stream().anyMatch(dictado -> dictado.getAsignatura().equals(asignaturaGrupo));

            // Si el profesor no dicta la asignatura, retorna false.
            if (!dictaAsignatura) {
                return false;
            }
        }
        return true; // Si todos los profesores dictan la asignatura correspondiente, retorna true.
    }

    // Método que verifica si se cumple la disponibilidad horaria de los profesores en el cromosoma.
    public boolean disponibilidadProfesorCumplida(Cromosoma cromosoma) {
        for (Gen gen : cromosoma.getGenes()) {
            // Si el profesor no está disponible en el horario asignado, retorna false.
            if (!profesorDisponible(gen.getProfesor(), gen.getDia(), gen.getHoraInicio(), gen.getHoraFin())) {
                return false;
            }
        }
        return true; // Si todos los profesores están disponibles en sus horarios asignados, retorna true.
    }

    // Método auxiliar que verifica si un profesor está disponible en un horario específico.
    private boolean profesorDisponible(Profesor profesor, String dia, Time horaInicio, Time horaFin) {
        List<HorarioProfesor> horariosProfesor = profesor.getHorarioProfesores();

        for (HorarioProfesor horario : horariosProfesor) {
            if (horario.getDisponibilidadHoraria().getDia().equals(dia)) {
                Time horaInicioDisponibilidad = horario.getDisponibilidadHoraria().getHoraInicio();
                Time horaFinDisponibilidad = horario.getDisponibilidadHoraria().getHoraFin();

                // Verifica si el horario solicitado está dentro de la disponibilidad del profesor.
                if (horaInicio.after(horaInicioDisponibilidad) || horaInicio.equals(horaInicioDisponibilidad)) {
                    if (horaFin.before(horaFinDisponibilidad) || horaFin.equals(horaFinDisponibilidad)) {
                        return true;
                    }
                }
            }
        }
        return false; // Retorna, false si el profesor no está disponible en el horario especificado.
    }

    // Método que verifica si hay solapamientos de profesores en un cromosoma.
    public boolean haySolapamientoProfesor(Cromosoma cromosoma) {
        for (int i = 0; i < cromosoma.getGenes().size(); i++) {
            Gen gen1 = cromosoma.getGenes().get(i);
            for (int j = i + 1; j < cromosoma.getGenes().size(); j++) {
                Gen gen2 = cromosoma.getGenes().get(j);
                if (gen1.getProfesor().equals(gen2.getProfesor()) && gen1.getDia().equals(gen2.getDia()) && !sonGruposDiferentesMismaMateria(gen1.getGrupo(), gen2.getGrupo()) && timeOverlap(gen1.getHoraInicio(), gen1.getHoraFin(), gen2.getHoraInicio(), gen2.getHoraFin())) {
                    return true; // Retorna true si hay un solapamiento de profesores.
                }
            }
        }
        return false; // Retorna, false si no se detectan solapamientos de profesores.
    }

    // Método que verifica si hay solapamientos de aulas en un cromosoma.
    public boolean haySolapamientoAula(Cromosoma cromosoma) {
        for (int i = 0; i < cromosoma.getGenes().size(); i++) {
            Gen gen1 = cromosoma.getGenes().get(i);
            for (int j = i + 1; j < cromosoma.getGenes().size(); j++) {
                Gen gen2 = cromosoma.getGenes().get(j);
                if (gen1.getAula().equals(gen2.getAula()) && gen1.getDia().equals(gen2.getDia()) && timeOverlap(gen1.getHoraInicio(), gen1.getHoraFin(), gen2.getHoraInicio(), gen2.getHoraFin())) {
                    return true; // Retorna true si hay un solapamiento de aulas.
                }
            }
        }
        return false; // Retorna, false si no se detectan solapamientos de aulas.
    }

    // Método que verifica si se cumplen las horas de teoría asignadas para cada grupo en el cromosoma.
    public boolean cumpleHorasTeoria(Cromosoma cromosoma) {
        for (Gen gen : cromosoma.getGenes()) {
            Grupo grupo = gen.getGrupo();
            int horasAsignadas = calcularHorasTeoriaAsignadas(gen);
            if (horasAsignadas < grupo.getAsignatura().getHorasTeoria()) {
                return false; // Retorna false si no se cumplen las horas de teoría requeridas.
            }
        }
        return true; // Retorna true si se cumplen las horas de teoría en todos los grupos.
    }

    // Método que verifica si las aulas asignadas cumplen con los requisitos (e.g., si necesitan computadoras).
    public boolean aulasCorrectasAsignadas(Cromosoma cromosoma) {
        for (Gen gen : cromosoma.getGenes()) {
            if (gen.getGrupo().getAsignatura().getNecesitaComputadores() && !gen.getAula().getTieneComputadores()) {
                return false; // Retorna false si un aula asignada no cumple con los requisitos de la asignatura.
            }
        }
        return true; // Retorna true si todas las aulas cumplen con los requisitos.
    }

    // Método auxiliar que calcula el número de horas de teoría asignadas a un grupo.
    private int calcularHorasTeoriaAsignadas(Gen gen) {
        long durationInMillis = gen.getHoraFin().getTime() - gen.getHoraInicio().getTime(); // Calcula la duración en milisegundos.
        return (int) (durationInMillis / (1000 * 60 * 60)); // Convierte la duración de milisegundos a horas y la retorna.
    }

    // Método auxiliar que verifica si hay solapamiento entre dos horarios.
    private boolean timeOverlap(Time start1, Time end1, Time start2, Time end2) {
        return start1.before(end2) && start2.before(end1); // Retorna true si los horarios se solapan.
    }

    // Método auxiliar que verifica si dos grupos diferentes pertenecen a la misma asignatura.
    private boolean sonGruposDiferentesMismaMateria(Grupo grupo1, Grupo grupo2) {
        return grupo1.getAsignatura().getIdAsignatura().equals(grupo2.getAsignatura().getIdAsignatura()) && !grupo1.getIdGrupo().equals(grupo2.getIdGrupo()); // Retorna true si los grupos son diferentes pero pertenecen a la misma asignatura.
    }
}
