package uis.horariouis.verification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.model.*;
import uis.horariouis.service.AulaService;

import java.sql.Time;
import java.util.List;
import java.util.Random;

@Service
public class ReparacionAlgoritmoGenetico {

    @Autowired
    private AulaService aulaService;

    // Método de corrección de solapamientos
    // (manténlo si planeas usarlo, si no, elimínalo si no tiene propósito)
    public void corregirSolapamientos(List<Gen> genes) {
        corregirSolapamientoAula(genes);
        corregirSolapamientoProfesor(genes);
    }

    public void corregirSolapamientoAula(List<Gen> genes) {
        for (int i = 0; i < genes.size(); i++) {
            Gen g1 = genes.get(i);
            for (int j = i + 1; j < genes.size(); j++) {
                Gen g2 = genes.get(j);
                if (g1.getAula().equals(g2.getAula()) &&
                        g1.getDia().equals(g2.getDia()) &&
                        timeOverlap(g1.getHoraInicio(), g1.getHoraFin(), g2.getHoraInicio(), g2.getHoraFin())) {

                    reubicarGen(g2, genes);
                }
            }
        }
    }

    public void corregirSolapamientoProfesor(List<Gen> genes) {
        for (int i = 0; i < genes.size(); i++) {
            Gen g1 = genes.get(i);
            for (int j = i + 1; j < genes.size(); j++) {
                Gen g2 = genes.get(j);
                if (g1.getProfesor().equals(g2.getProfesor()) &&
                        g1.getDia().equals(g2.getDia()) &&
                        timeOverlap(g1.getHoraInicio(), g1.getHoraFin(), g2.getHoraInicio(), g2.getHoraFin())) {

                    reubicarGen(g2, genes);
                }
            }
        }
    }

    private void reubicarGen(Gen gen, List<Gen> genes) {
        String nuevoDia;
        Time nuevaHoraInicio;
        Time nuevaHoraFin;
        boolean conflictivo;

        do {
            conflictivo = false;
            nuevoDia = obtenerDiaAleatorio();
            nuevaHoraInicio = obtenerHoraInicioAleatoria();
            nuevaHoraFin = Time.valueOf(String.format("%02d:00:00", nuevaHoraInicio.toLocalTime().getHour() + 2));

            for (Gen g : genes) {
                if (!g.equals(gen) && g.getDia().equals(nuevoDia) &&
                        timeOverlap(nuevaHoraInicio, nuevaHoraFin, g.getHoraInicio(), g.getHoraFin())) {
                    conflictivo = true;
                    break;
                }
            }
        } while (conflictivo);

        gen.setDia(nuevoDia);
        gen.setHoraInicio(nuevaHoraInicio);
        gen.setHoraFin(nuevaHoraFin);

        // También podrías cambiar el aula si es necesario
        gen.setAula(obtenerAulaAleatoria(gen.getGrupo().getAsignatura().getNecesitaComputadores()));
    }

    private boolean timeOverlap(Time start1, Time end1, Time start2, Time end2) {
        return start1.before(end2) && start2.before(end1);
    }

    private String obtenerDiaAleatorio() {
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"};
        return dias[new Random().nextInt(dias.length)];
    }

    private Time obtenerHoraInicioAleatoria() {
        int hora = 6 + new Random().nextInt(14);  // 14 horas disponibles de 6:00 AM a 8:00 PM
        return Time.valueOf(String.format("%02d:00:00", hora));
    }

    private Aula obtenerAulaAleatoria(boolean necesitaComputadores) {
        return aulaService.obtenerAulaAleatoria(necesitaComputadores);
    }
}
