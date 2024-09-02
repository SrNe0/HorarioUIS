package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.model.Grupo;
import uis.horariouis.model.Horario;
import uis.horariouis.repository.HorarioRepository;

import java.sql.Time;
import java.util.List;

@Service // Marca esta clase como un servicio de Spring para que pueda ser inyectada en otros componentes.
public class AjusteHorasService {
//    Servicio encargado de ajustar los horarios para que no excedan las 5 horas de teoría asignadas por grupo.

    @Autowired // Inyecta una instancia del repositorio de horarios para interactuar con la base de datos.
    private HorarioRepository horarioRepository;

    // Método principal que ajusta los horarios para los grupos que tienen 5 horas de teoría asignadas.
    public void ajustarHorariosDe5Horas() {
        // Obtiene una lista de grupos que tienen 5 horas de teoría asignadas.
        List<Grupo> gruposCon5HorasTeoria = horarioRepository.findGruposCon5HorasTeoria();

        // Recorre cada grupo para ajustar sus horarios si es necesario.
        for (Grupo grupo : gruposCon5HorasTeoria) {
            // Obtiene la lista de horarios asociados al grupo actual.
            List<Horario> horariosDelGrupo = horarioRepository.findByGrupo(grupo);
            // Calcula el total de horas asignadas a este grupo sumando la duración de cada horario.
            int horasTotales = horariosDelGrupo.stream().mapToInt(this::calcularDuracionEnHoras).sum();

            // Si el total de horas es mayor a 5, se procede a ajustar.
            if (horasTotales > 5) {
                int exceso = horasTotales - 5; // Calcula el exceso de horas asignadas.
                ajustarHoras(horariosDelGrupo, exceso); // Ajusta las horas para reducir el exceso.
            }
        }
    }

    // Método que ajusta las horas de un grupo para que no excedan las 5 horas.
    private void ajustarHoras(List<Horario> horarios, int exceso) {
        // Recorre los horarios para reducir el tiempo hasta que el exceso desaparezca.
        for (Horario horario : horarios) {
            int duracionEnHoras = calcularDuracionEnHoras(horario); // Calcula la duración del horario actual en horas.
            // Si la duración es mayor a 1 hora y aún hay exceso, se procede a reducir.
            if (duracionEnHoras > 1 && exceso > 0) {
                // Determina cuántas horas se pueden reducir en este horario sin dejarlo en menos de una hora.
                int horasAReducir = Math.min(exceso, duracionEnHoras - 1);
                reducirDuracionDeHorario(horario, horasAReducir); // Reduce la duración del horario.
                exceso -= horasAReducir; // Actualiza el exceso restante.
            }
            // Si ya no hay exceso, se detiene el proceso.
            if (exceso <= 0) {
                break;
            }
        }
    }

    // Calcula la duración de un horario en horas.
    private int calcularDuracionEnHoras(Horario horario) {
        // Calcula la duración en milisegundos entre la hora de inicio y la hora de fin.
        long duracionEnMillis = horario.getHoraFin().getTime() - horario.getHoraInicio().getTime();
        // Convierte la duración a horas y la retorna.
        return (int) (duracionEnMillis / (1000 * 60 * 60));
    }

    // Reduce la duración de un horario en una cantidad específica de horas.
    private void reducirDuracionDeHorario(Horario horario, int horasAReducir) {
        // Calcula la nueva hora de fin restando las horas a reducir de la hora de fin actual.
        Time nuevaHoraFin = new Time(horario.getHoraFin().getTime() - (long) horasAReducir * 60 * 60 * 1000);
        horario.setHoraFin(nuevaHoraFin); // Establece la nueva hora de fin en el horario.
        horarioRepository.save(horario); // Guarda el horario actualizado en la base de datos.

        // Imprime un mensaje indicando cuántas horas se redujeron del horario.
        System.out.println("Reducida " + horasAReducir + " hora(s) del horario de " + horario.getGrupo().getAsignatura().getNombre() + " el día " + horario.getDia() + " de " + horario.getHoraInicio() + " a " + horario.getHoraFin());
    }
}
