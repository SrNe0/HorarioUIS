package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.model.Horario;
import uis.horariouis.model.Grupo;
import uis.horariouis.repository.HorarioRepository;
import uis.horariouis.verification.VerificadorRestricciones;

import java.sql.Time;
import java.util.List;
import java.util.Random;

@Service
public class CorregirHorariosService {

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private VerificadorRestricciones verificadorRestricciones;

    // Método principal para corregir conflictos en la base de datos
    public void corregirHorariosConflictivos(List<Grupo> grupos) {
        List<Horario> horarios = horarioRepository.findAll();

        // Verificar y corregir solapamientos de aulas
        if (!verificadorRestricciones.verificarSolapamientoAulas(horarios, new StringBuilder())) {
            corregirSolapamientos(horarios);
        }

        // Verificar y corregir solapamientos de profesores
        if (!verificadorRestricciones.verificarSolapamientoProfesores(horarios, new StringBuilder())) {
            corregirSolapamientos(horarios);
        }

        // Guardar los horarios corregidos en la base de datos
        horarioRepository.saveAll(horarios);
    }

    // Método para corregir solapamientos reubicando los horarios conflictivos
    private void corregirSolapamientos(List<Horario> horarios) {
        for (int i = 0; i < horarios.size(); i++) {
            Horario h1 = horarios.get(i);
            for (int j = i + 1; j < horarios.size(); j++) {
                Horario h2 = horarios.get(j);

                if (h1.getAula().equals(h2.getAula()) &&
                        h1.getDia().equals(h2.getDia()) &&
                        timeOverlap(h1.getHoraInicio(), h1.getHoraFin(), h2.getHoraInicio(), h2.getHoraFin())) {

                    // Reubicar uno de los horarios conflictivos
                    reubicarHorario(h2, horarios);
                }

                if (h1.getProfesor().equals(h2.getProfesor()) &&
                        h1.getDia().equals(h2.getDia()) &&
                        timeOverlap(h1.getHoraInicio(), h1.getHoraFin(), h2.getHoraInicio(), h2.getHoraFin())) {

                    // Reubicar uno de los horarios conflictivos
                    reubicarHorario(h2, horarios);
                }
            }
        }
    }

    // Método que intenta reubicar un horario conflictivo
    private void reubicarHorario(Horario horario, List<Horario> horarios) {
        String nuevoDia;
        Time nuevaHoraInicio;
        Time nuevaHoraFin;
        boolean conflictivo;
        int intentos = 0;

        do {
            conflictivo = false;
            nuevoDia = obtenerDiaAleatorio();
            nuevaHoraInicio = obtenerHoraInicioAleatoria();
            nuevaHoraFin = Time.valueOf(String.format("%02d:00:00", nuevaHoraInicio.toLocalTime().getHour() + calcularDuracionHoras(horario.getHoraInicio(), horario.getHoraFin())));

            for (Horario h : horarios) {
                if (h.getDia().equals(nuevoDia) &&
                        timeOverlap(nuevaHoraInicio, nuevaHoraFin, h.getHoraInicio(), h.getHoraFin())) {
                    conflictivo = true;
                    break;
                }
            }

            intentos++;
            if (intentos > 100) {
                System.out.println("No se pudo encontrar una reubicación sin solapamientos después de 100 intentos.");
                break;
            }
        } while (conflictivo);

        if (!conflictivo) {
            horario.setDia(nuevoDia);
            horario.setHoraInicio(nuevaHoraInicio);
            horario.setHoraFin(nuevaHoraFin);
        }
    }

    // Método auxiliar para obtener un día aleatorio
    private String obtenerDiaAleatorio() {
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"};
        return dias[new Random().nextInt(dias.length)];
    }

    // Método auxiliar para obtener una hora de inicio aleatoria
    private Time obtenerHoraInicioAleatoria() {
        int hora = 6 + new Random().nextInt(14);
        return Time.valueOf(String.format("%02d:00:00", hora));
    }

    // Método auxiliar para calcular la duración en horas entre dos instantes de tiempo
    private int calcularDuracionHoras(Time horaInicio, Time horaFin) {
        long durationInMillis = horaFin.getTime() - horaInicio.getTime();
        return (int) (durationInMillis / (1000 * 60 * 60));
    }

    // Método auxiliar para verificar si dos horarios se solapan
    private boolean timeOverlap(Time start1, Time end1, Time start2, Time end2) {
        return start1.before(end2) && start2.before(end1);
    }
}
