package uis.horariouis.verification;

import org.springframework.stereotype.Service;
import uis.horariouis.model.*;

import java.sql.Time;
import java.util.List;

@Service
public class RestriccionesAlgoritmoGenetico {

    public int calcularAptitud(Cromosoma cromosoma) {
        int aptitud = 0;

        if (!haySolapamientoProfesor(cromosoma)) {
            aptitud += 20;
        } else {
            aptitud -= 50;
        }

        if (!haySolapamientoAula(cromosoma)) {
            aptitud += 20;
        } else {
            aptitud -= 50;
        }

        if (cumpleHorasTeoria(cromosoma)) {
            aptitud += 20;
        } else {
            aptitud -= 30;
        }

        if (aulasCorrectasAsignadas(cromosoma)) {
            aptitud += 20;
        } else {
            aptitud -= 40;
        }

        if (disponibilidadProfesorCumplida(cromosoma)) {
            aptitud += 20;
        } else {
            aptitud -= 40;
        }

        if (profesorDictaAsignaturaDelGrupo(cromosoma)) {
            aptitud += 20;
        } else {
            aptitud -= 50;
        }

        return aptitud;
    }

    public boolean profesorDictaAsignaturaDelGrupo(Cromosoma cromosoma) {
        for (Gen gen : cromosoma.getGenes()) {
            Profesor profesor = gen.getProfesor();
            Asignatura asignaturaGrupo = gen.getGrupo().getAsignatura();

            boolean dictaAsignatura = profesor.getDictados().stream()
                    .anyMatch(dictado -> dictado.getAsignatura().equals(asignaturaGrupo));

            if (!dictaAsignatura) {
                return false;
            }
        }
        return true;
    }

    public boolean disponibilidadProfesorCumplida(Cromosoma cromosoma) {
        for (Gen gen : cromosoma.getGenes()) {
            if (!profesorDisponible(gen.getProfesor(), gen.getDia(), gen.getHoraInicio(), gen.getHoraFin())) {
                return false;
            }
        }
        return true;
    }

    private boolean profesorDisponible(Profesor profesor, String dia, Time horaInicio, Time horaFin) {
        List<HorarioProfesor> horariosProfesor = profesor.getHorarioProfesores();

        for (HorarioProfesor horario : horariosProfesor) {
            if (horario.getDisponibilidadHoraria().getDia().equals(dia)) {
                Time horaInicioDisponibilidad = horario.getDisponibilidadHoraria().getHoraInicio();
                Time horaFinDisponibilidad = horario.getDisponibilidadHoraria().getHoraFin();

                if (horaInicio.after(horaInicioDisponibilidad) || horaInicio.equals(horaInicioDisponibilidad)) {
                    if (horaFin.before(horaFinDisponibilidad) || horaFin.equals(horaFinDisponibilidad)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean haySolapamientoProfesor(Cromosoma cromosoma) {
        for (int i = 0; i < cromosoma.getGenes().size(); i++) {
            Gen gen1 = cromosoma.getGenes().get(i);
            for (int j = i + 1; j < cromosoma.getGenes().size(); j++) {
                Gen gen2 = cromosoma.getGenes().get(j);
                if (gen1.getProfesor().equals(gen2.getProfesor()) &&
                        gen1.getDia().equals(gen2.getDia()) &&
                        !sonGruposDiferentesMismaMateria(gen1.getGrupo(), gen2.getGrupo()) &&
                        timeOverlap(gen1.getHoraInicio(), gen1.getHoraFin(), gen2.getHoraInicio(), gen2.getHoraFin())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean haySolapamientoAula(Cromosoma cromosoma) {
        for (int i = 0; i < cromosoma.getGenes().size(); i++) {
            Gen gen1 = cromosoma.getGenes().get(i);
            for (int j = i + 1; j < cromosoma.getGenes().size(); j++) {
                Gen gen2 = cromosoma.getGenes().get(j);
                if (gen1.getAula().equals(gen2.getAula()) &&
                        gen1.getDia().equals(gen2.getDia()) &&
                        timeOverlap(gen1.getHoraInicio(), gen1.getHoraFin(), gen2.getHoraInicio(), gen2.getHoraFin())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean cumpleHorasTeoria(Cromosoma cromosoma) {
        for (Gen gen : cromosoma.getGenes()) {
            Grupo grupo = gen.getGrupo();
            int horasAsignadas = calcularHorasTeoriaAsignadas(gen);
            if (horasAsignadas < grupo.getAsignatura().getHorasTeoria()) {
                return false;
            }
        }
        return true;
    }

    public boolean aulasCorrectasAsignadas(Cromosoma cromosoma) {
        for (Gen gen : cromosoma.getGenes()) {
            if (gen.getGrupo().getAsignatura().getNecesitaComputadores() && !gen.getAula().getTieneComputadores()) {
                return false;
            }
        }
        return true;
    }

    private int calcularHorasTeoriaAsignadas(Gen gen) {
        long durationInMillis = gen.getHoraFin().getTime() - gen.getHoraInicio().getTime();
        return (int) (durationInMillis / (1000 * 60 * 60));
    }

    private boolean timeOverlap(Time start1, Time end1, Time start2, Time end2) {
        return start1.before(end2) && start2.before(end1);
    }

    private boolean sonGruposDiferentesMismaMateria(Grupo grupo1, Grupo grupo2) {
        return grupo1.getAsignatura().getIdAsignatura().equals(grupo2.getAsignatura().getIdAsignatura()) &&
                !grupo1.getIdGrupo().equals(grupo2.getIdGrupo());
    }
}
