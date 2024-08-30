package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.model.Grupo;
import uis.horariouis.model.Horario;
import uis.horariouis.repository.HorarioRepository;

import java.sql.Time;
import java.util.Comparator;
import java.util.List;

@Service
public class AjusteHorasService {

    @Autowired
    private HorarioRepository horarioRepository;

    public void ajustarHorariosDe5Horas() {
        List<Grupo> gruposCon5HorasTeoria = horarioRepository.findGruposCon5HorasTeoria();

        for (Grupo grupo : gruposCon5HorasTeoria) {
            List<Horario> horariosDelGrupo = horarioRepository.findByGrupo(grupo);
            int horasTotales = horariosDelGrupo.stream().mapToInt(this::calcularDuracionEnHoras).sum();

            if (horasTotales > 5) {
                int exceso = horasTotales - 5;
                ajustarHoras(horariosDelGrupo, exceso);
            }
        }
    }

    private void ajustarHoras(List<Horario> horarios, int exceso) {
        for (Horario horario : horarios) {
            int duracionEnHoras = calcularDuracionEnHoras(horario);
            if (duracionEnHoras > 1 && exceso > 0) {
                int horasAReducir = Math.min(exceso, duracionEnHoras - 1);
                reducirDuracionDeHorario(horario, horasAReducir);
                exceso -= horasAReducir;
            }
            if (exceso <= 0) {
                break;
            }
        }
    }

    private int calcularDuracionEnHoras(Horario horario) {
        long duracionEnMillis = horario.getHoraFin().getTime() - horario.getHoraInicio().getTime();
        return (int) (duracionEnMillis / (1000 * 60 * 60));
    }

    private void reducirDuracionDeHorario(Horario horario, int horasAReducir) {
        Time nuevaHoraFin = new Time(horario.getHoraFin().getTime() - horasAReducir * 60 * 60 * 1000);
        horario.setHoraFin(nuevaHoraFin);
        horarioRepository.save(horario);

        System.out.println("Reducida " + horasAReducir + " hora(s) del horario de " + horario.getGrupo().getAsignatura().getNombre() + " el día " + horario.getDia() + " de " + horario.getHoraInicio() + " a " + horario.getHoraFin());
    }
}
