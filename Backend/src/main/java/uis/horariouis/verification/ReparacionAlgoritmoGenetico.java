package uis.horariouis.verification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.model.*;
import uis.horariouis.service.AulaService;

import java.sql.Time;
import java.util.List;
import java.util.Random;

@Service // Anotación que marca esta clase como un servicio de Spring, permitiendo su inyección en otros componentes.
public class ReparacionAlgoritmoGenetico {
//   Esta clase está diseñada para corregir solapamientos en horarios
//    generados por un algoritmo genético, asegurando que no haya conflictos en las asignaciones de aulas y profesores.


    @Autowired
    private AulaService aulaService; // Servicio que permite acceder a la lógica relacionada con las aulas.

    // Método de corrección de solapamientos
    // (manténlo si planeas usarlo, si no, elimínalo si no tiene propósito)
    public void corregirSolapamientos(List<Gen> genes) {
        corregirSolapamientoAula(genes);
        corregirSolapamientoProfesor(genes);
    }

    // Método para corregir solapamientos de aulas en una lista de genes.
    public void corregirSolapamientoAula(List<Gen> genes) {
        // Itera sobre todos los genes para comparar posibles solapamientos.
        for (int i = 0; i < genes.size(); i++) {
            Gen g1 = genes.get(i);
            for (int j = i + 1; j < genes.size(); j++) {
                Gen g2 = genes.get(j);
                // Verifica si los genes tienen la misma aula, día y si sus horarios se solapan.
                if (g1.getAula().equals(g2.getAula()) &&
                        g1.getDia().equals(g2.getDia()) &&
                        timeOverlap(g1.getHoraInicio(), g1.getHoraFin(), g2.getHoraInicio(), g2.getHoraFin())) {

                    reubicarGen(g2, genes); // Si hay solapamiento, intenta reubicar el segundo gen.
                }
            }
        }
    }

    // Método para corregir solapamientos de profesores en una lista de genes.
    public void corregirSolapamientoProfesor(List<Gen> genes) {
        // Itera sobre todos los genes para comparar posibles solapamientos.
        for (int i = 0; i < genes.size(); i++) {
            Gen g1 = genes.get(i);
            for (int j = i + 1; j < genes.size(); j++) {
                Gen g2 = genes.get(j);
                // Verifica si los genes tienen el mismo profesor, día y si sus horarios se solapan.
                if (g1.getProfesor().equals(g2.getProfesor()) &&
                        g1.getDia().equals(g2.getDia()) &&
                        timeOverlap(g1.getHoraInicio(), g1.getHoraFin(), g2.getHoraInicio(), g2.getHoraFin())) {

                    reubicarGen(g2, genes); // Si hay solapamiento, intenta reubicar el segundo gen.
                }
            }
        }
    }

    // Método que intenta reubicar un gen para evitar solapamientos.
    private void reubicarGen(Gen gen, List<Gen> genes) {
        String nuevoDia;
        Time nuevaHoraInicio;
        Time nuevaHoraFin;
        boolean conflictivo;

        do {
            conflictivo = false; // Inicializa la bandera de conflicto.
            nuevoDia = obtenerDiaAleatorio(); // Selecciona un día aleatorio.
            nuevaHoraInicio = obtenerHoraInicioAleatoria(); // Selecciona una hora de inicio aleatoria.
            nuevaHoraFin = Time.valueOf(String.format("%02d:00:00", nuevaHoraInicio.toLocalTime().getHour() + 2)); // Calcula la hora de fin sumando 2 horas a la hora de inicio.

            // Verifica si el nuevo horario entra en conflicto con otros genes en el mismo día.
            for (Gen g : genes) {
                if (!g.equals(gen) && g.getDia().equals(nuevoDia) &&
                        timeOverlap(nuevaHoraInicio, nuevaHoraFin, g.getHoraInicio(), g.getHoraFin())) {
                    conflictivo = true; // Marca como conflictivo si se detecta solapamiento.
                    break;
                }
            }
        } while (conflictivo); // Repite hasta encontrar un horario sin conflictos.

        // Actualiza el gen con el nuevo día y horario.
        gen.setDia(nuevoDia);
        gen.setHoraInicio(nuevaHoraInicio);
        gen.setHoraFin(nuevaHoraFin);

        // También actualiza el aula si es necesario.
        gen.setAula(obtenerAulaAleatoria(gen.getGrupo().getAsignatura().getNecesitaComputadores()));
    }

    // Método auxiliar para verificar si dos intervalos de tiempo se solapan.
    private boolean timeOverlap(Time start1, Time end1, Time start2, Time end2) {
        return start1.before(end2) && start2.before(end1); // Retorna true si los horarios se solapan.
    }

    // Método que obtiene un día aleatorio de la semana.
    private String obtenerDiaAleatorio() {
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"};
        return dias[new Random().nextInt(dias.length)]; // Selecciona y retorna un día aleatorio.
    }

    // Método que obtiene una hora de inicio aleatoria entre 6:00 AM y 8:00 PM.
    private Time obtenerHoraInicioAleatoria() {
        int hora = 6 + new Random().nextInt(14);  // Genera una hora aleatoria entre las 6:00 AM y las 8:00 PM (14 horas disponibles).
        return Time.valueOf(String.format("%02d:00:00", hora)); // Retorna la hora en formato Time.
    }

    // Método que obtiene un aula aleatoria basada en si necesita computadoras.
    private Aula obtenerAulaAleatoria(boolean necesitaComputadores) {
        return aulaService.obtenerAulaAleatoria(necesitaComputadores); // Usa el servicio de aula para obtener un aula aleatoria.
    }
    public void reubicarGenDesdeHorario(Horario horario, List<Horario> horarios) {
        String nuevoDia;
        Time nuevaHoraInicio;
        Time nuevaHoraFin;
        boolean conflictivo;

        do {
            conflictivo = false;
            nuevoDia = obtenerDiaAleatorio();
            nuevaHoraInicio = obtenerHoraInicioAleatoria();
            nuevaHoraFin = Time.valueOf(String.format("%02d:00:00", nuevaHoraInicio.toLocalTime().getHour() + 2));

            for (Horario h : horarios) {
                if (!h.equals(horario) && h.getDia().equals(nuevoDia) &&
                        timeOverlap(nuevaHoraInicio, nuevaHoraFin, h.getHoraInicio(), h.getHoraFin())) {
                    conflictivo = true;
                    break;
                }
            }
        } while (conflictivo);

        horario.setDia(nuevoDia);
        horario.setHoraInicio(nuevaHoraInicio);
        horario.setHoraFin(nuevaHoraFin);

        // También podrías cambiar el aula si es necesario
        horario.setAula(obtenerAulaAleatoria(horario.getGrupo().getAsignatura().getNecesitaComputadores()));
    }

}
