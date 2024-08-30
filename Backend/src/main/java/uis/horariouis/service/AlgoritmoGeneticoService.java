package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.model.*;
import uis.horariouis.repository.HorarioRepository;

import java.sql.Time;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AlgoritmoGeneticoService {

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private AulaService aulaService;

    @Autowired
    private HorarioRepository horarioRepository;  // Inyección del repositorio de Horario

    @Autowired
    private AjusteHorasService ajusteHorasService;
    private final List<String> logs = new ArrayList<>();

    public void ejecutarAlgoritmoGenetico() {
        // Asignar y guardar primero los bloques de 5 horas
        asignarYGuardarBloquesDe5Horas();

        // Luego, asignar y guardar los bloques de 4 horas
        asignarYGuardarBloquesDe4Horas();

        ajusteHorasService.ajustarHorariosDe5Horas();

        // Mostrar todos los logs al final del proceso
        mostrarLogs();
    }

    private void asignarYGuardarBloquesDe5Horas() {
        List<Grupo> grupos5Horas = grupoService.obtenerGruposConProfesor().stream()
                .filter(grupo -> grupo.getAsignatura() != null && grupo.getAsignatura().getHorasTeoria() == 5)
                .collect(Collectors.toList());

        List<Cromosoma> poblacion5Horas = inicializarPoblacion5Horas(grupos5Horas);

        int generaciones = 200;
        for (int i = 0; i < generaciones; i++) {
            poblacion5Horas.forEach(this::calcularAptitud);
            List<Cromosoma> seleccionados = seleccionar(poblacion5Horas);
            List<Cromosoma> nuevaPoblacion = cruzar(seleccionados);
            nuevaPoblacion.forEach(this::mutar);
            poblacion5Horas = nuevaPoblacion;
        }

        Cromosoma mejorSolucion5Horas = encontrarMejorSolucion(poblacion5Horas);
        guardarMejorSolucion(mejorSolucion5Horas);
    }

    private void asignarYGuardarBloquesDe4Horas() {
        List<Grupo> grupos4Horas = grupoService.obtenerGruposConProfesor().stream()
                .filter(grupo -> grupo.getAsignatura() != null && grupo.getAsignatura().getHorasTeoria() == 4)
                .collect(Collectors.toList());

        List<Cromosoma> poblacion4Horas = inicializarPoblacion4Horas(grupos4Horas);

        int generaciones = 200;
        for (int i = 0; i < generaciones; i++) {
            poblacion4Horas.forEach(this::calcularAptitud);
            List<Cromosoma> seleccionados = seleccionar(poblacion4Horas);
            List<Cromosoma> nuevaPoblacion = cruzar(seleccionados);
            nuevaPoblacion.forEach(this::mutar);
            poblacion4Horas = nuevaPoblacion;
        }

        Cromosoma mejorSolucion4Horas = encontrarMejorSolucion(poblacion4Horas);
        guardarMejorSolucion(mejorSolucion4Horas);
    }

    private List<Cromosoma> inicializarPoblacion5Horas(List<Grupo> grupos) {
        List<Cromosoma> poblacion = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 100; i++) {
            List<Gen> genes = new ArrayList<>();
            for (Grupo grupo : grupos) {
                Profesor profesor = grupo.getAsignatura().getDictados().stream().findFirst().orElseThrow().getProfesor();
                Aula aula = obtenerAulaAleatoria(grupo.getAsignatura().getNecesitaComputadores());

                // Asignar el primer bloque de 2 horas
                String dia1;
                Time horaInicio1;
                Time horaFin1;
                do {
                    dia1 = obtenerDiaAleatorio(genes, grupo);
                    horaInicio1 = obtenerHoraInicioAleatoria(14);
                    horaFin1 = Time.valueOf(String.format("%02d:00:00", horaInicio1.toLocalTime().getHour() + 2));
                } while (!esProfesorDisponible(profesor, dia1, horaInicio1, horaFin1));

                Gen gen1 = new Gen(grupo, profesor, aula, dia1, horaInicio1, horaFin1);
                genes.add(gen1);

                logs.add("Asignado bloque de 2 horas: Inicio " + horaInicio1 + ", Fin: " + horaFin1 + " al grupo " + grupo.getIdGrupo());

                // Asignar el segundo bloque de 2 horas en un día diferente
                String dia2;
                Time horaInicio2;
                Time horaFin2;
                do {
                    dia2 = obtenerDiaAleatorio(genes, grupo);
                    horaInicio2 = obtenerHoraInicioAleatoria(14);
                    horaFin2 = Time.valueOf(String.format("%02d:00:00", horaInicio2.toLocalTime().getHour() + 2));
                } while (!esProfesorDisponible(profesor, dia2, horaInicio2, horaFin2) || dia2.equals(dia1));

                Gen gen2 = new Gen(grupo, profesor, aula, dia2, horaInicio2, horaFin2);
                genes.add(gen2);

                logs.add("Asignado segundo bloque de 2 horas: Inicio " + horaInicio2 + ", Fin: " + horaFin2 + " al grupo " + grupo.getIdGrupo());

                // Asignar el bloque final de 1 hora en un día diferente
                String dia3;
                Time horaInicio3;
                Time horaFin3;
                do {
                    dia3 = obtenerDiaAleatorio(genes, grupo);
                    horaInicio3 = obtenerHoraInicioAleatoria(21);
                    horaFin3 = Time.valueOf(String.format("%02d:00:00", horaInicio3.toLocalTime().getHour() + 1));
                } while (!esProfesorDisponible(profesor, dia3, horaInicio3, horaFin3) || dia3.equals(dia1) || dia3.equals(dia2));

                Gen gen3 = new Gen(grupo, profesor, aula, dia3, horaInicio3, horaFin3);
                genes.add(gen3);

                logs.add("Asignado bloque de 1 hora: Inicio " + horaInicio3 + ", Fin: " + horaFin3 + " al grupo " + grupo.getIdGrupo());
            }
            poblacion.add(new Cromosoma(genes));
            System.out.println("Cromosoma " + i + " generado con " + genes.size() + " genes.");
        }
        return poblacion;
    }
    private List<Cromosoma> inicializarPoblacion4Horas(List<Grupo> grupos) {
        List<Cromosoma> poblacion = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            List<Gen> genes = new ArrayList<>();
            for (Grupo grupo : grupos) {
                Profesor profesor = grupo.getAsignatura().getDictados().stream().findFirst().orElseThrow().getProfesor();
                Aula aula = obtenerAulaAleatoria(grupo.getAsignatura().getNecesitaComputadores());

                // Asignar dos bloques de 2 horas
                for (int j = 0; j < 2; j++) {
                    String dia;
                    Time horaInicio;
                    Time horaFin;

                    do {
                        dia = obtenerDiaAleatorio(genes, grupo);
                        horaInicio = obtenerHoraInicioAleatoria(16); // Horas entre 6:00 y 20:00 para permitir 2 horas consecutivas
                        horaFin = Time.valueOf(String.format("%02d:00:00", horaInicio.toLocalTime().getHour() + 2));
                    } while (!esProfesorDisponible(profesor, dia, horaInicio, horaFin));

                    Gen gen = new Gen(grupo, profesor, aula, dia, horaInicio, horaFin);
                    genes.add(gen);

                    logs.add("Asignado bloque de 2 horas: Inicio " + horaInicio + ", Fin: " + horaFin + " al grupo " + grupo.getIdGrupo());
                }
            }
            poblacion.add(new Cromosoma(genes));
            System.out.println("Cromosoma " + i + " generado con " + genes.size() + " genes.");
        }
        return poblacion;
    }


    private boolean esProfesorDisponible(Profesor profesor, String dia, Time horaInicio, Time horaFin) {
        List<HorarioProfesor> horariosProfesor = profesor.getHorarioProfesores();

        // Itera sobre cada hora dentro del bloque solicitado
        for (int hora = horaInicio.toLocalTime().getHour(); hora < horaFin.toLocalTime().getHour(); hora++) {
            Time horaActualInicio = Time.valueOf(String.format("%02d:00:00", hora));
            Time horaActualFin = Time.valueOf(String.format("%02d:00:00", hora + 1));

            boolean disponible = horariosProfesor.stream().anyMatch(horarioProfesor ->
                    horarioProfesor.getDisponibilidadHoraria().getDia().equals(dia) &&
                            horarioProfesor.getDisponibilidadHoraria().getHoraInicio().equals(horaActualInicio) &&
                            horarioProfesor.getDisponibilidadHoraria().getHoraFin().equals(horaActualFin)
            );

            if (!disponible) {
                return false; // Si alguna hora no está disponible, retorna false
            }
        }

        return true; // Todas las horas dentro del bloque están disponibles
    }







    private void calcularAptitud(Cromosoma cromosoma) {
        int aptitud = 0;

        if (!haySolapamientoProfesor(cromosoma)) {
            aptitud += 20;  // Aumenta la penalización si no hay solapamiento
        } else {
            aptitud -= 50;  // Penaliza fuertemente si hay solapamiento
        }

        if (!haySolapamientoAula(cromosoma)) {
            aptitud += 20;
        } else {
            aptitud -= 50;
        }

        if (cumpleHorasTeoria(cromosoma)) {
            aptitud += 20;
        } else {
            aptitud -= 30;  // Penalización si no se cumplen las horas de teoría correctamente
        }

        if (aulasCorrectasAsignadas(cromosoma)) {
            aptitud += 20;
        } else {
            aptitud -= 40;  // Penalización si se asignan aulas incorrectas
        }

        if (disponibilidadProfesorCumplida(cromosoma)) {
            aptitud += 20;
        } else {
            aptitud -= 40;  // Penalización si el profesor no está disponible
        }

        cromosoma.setAptitud(aptitud);
    }

    private boolean disponibilidadProfesorCumplida(Cromosoma cromosoma) {
        for (Gen gen : cromosoma.getGenes()) {
            // Verifica que el profesor esté disponible durante las horas asignadas
            if (!profesorDisponible(gen.getProfesor(), gen.getDia(), gen.getHoraInicio(), gen.getHoraFin())) {
                return false;
            }
        }
        return true;
    }

    private boolean profesorDisponible(Profesor profesor, String dia, Time horaInicio, Time horaFin) {
        // Supongamos que tienes una lista de horarios de profesor
        List<HorarioProfesor> horariosProfesor = profesor.getHorarioProfesores(); // Asume que el profesor tiene una lista de horarios

        // Itera sobre los horarios del profesor para el día específico
        for (HorarioProfesor horario : horariosProfesor) {
            if (horario.getDisponibilidadHoraria().getDia().equals(dia)) {
                // Verifica si las horas de la asignación están completamente dentro de la disponibilidad del profesor
                Time horaInicioDisponibilidad = horario.getDisponibilidadHoraria().getHoraInicio();
                Time horaFinDisponibilidad = horario.getDisponibilidadHoraria().getHoraFin();

                if (horaInicio.after(horaInicioDisponibilidad) || horaInicio.equals(horaInicioDisponibilidad)) {
                    if (horaFin.before(horaFinDisponibilidad) || horaFin.equals(horaFinDisponibilidad)) {
                        return true; // El profesor está disponible durante todo el rango de horas
                    }
                }
            }
        }

        // Si no se encuentra una disponibilidad que cubra las horas solicitadas, retorna falso
        return false;
    }




    private boolean haySolapamientoProfesor(Cromosoma cromosoma) {
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

    private boolean timeOverlap(Time start1, Time end1, Time start2, Time end2) {
        return start1.before(end2) && start2.before(end1);
    }
    private boolean sonGruposDiferentesMismaMateria(Grupo grupo1, Grupo grupo2) {
        // Verifica si los dos grupos pertenecen a la misma asignatura pero son diferentes grupos
        return grupo1.getAsignatura().getIdAsignatura().equals(grupo2.getAsignatura().getIdAsignatura()) &&
                !grupo1.getIdGrupo().equals(grupo2.getIdGrupo());
    }


    private boolean haySolapamientoAula(Cromosoma cromosoma) {
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
    private String obtenerDiaAleatorio(List<Gen> genes, Grupo grupoActual) {
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"};
        Random random = new Random();
        String diaAsignado;
        boolean conflictivo;

        do {
            conflictivo = false;
            diaAsignado = dias[random.nextInt(dias.length)];
            for (Gen gen : genes) {
                // Evitar solapamientos para el mismo grupo en la misma asignatura
                if (gen.getGrupo().equals(grupoActual) &&
                        gen.getDia().equals(diaAsignado)) {
                    conflictivo = true;
                    break;
                }
            }
        } while (conflictivo);

        return diaAsignado;
    }

    private boolean cumpleHorasTeoria(Cromosoma cromosoma) {
        for (Grupo grupo : grupoService.obtenerGruposConProfesor()) {
            int horasAsignadas = 0;
            for (Gen gen : cromosoma.getGenes()) {
                if (gen.getGrupo().equals(grupo)) {
                    horasAsignadas += calcularHorasTeoriaAsignadas(gen);
                }
            }
            if (horasAsignadas < grupo.getAsignatura().getHorasTeoria()) {
                return false;
            }
        }
        return true;
    }

    private int calcularHorasTeoriaAsignadas(Gen gen) {
        long durationInMillis = gen.getHoraFin().getTime() - gen.getHoraInicio().getTime();
        return (int) (durationInMillis / (1000 * 60 * 60));
    }

    private boolean aulasCorrectasAsignadas(Cromosoma cromosoma) {
        for (Gen gen : cromosoma.getGenes()) {
            if (gen.getGrupo().getAsignatura().getNecesitaComputadores() && !gen.getAula().getTieneComputadores()) {
                return false;
            }
        }
        return true;
    }

    private List<Cromosoma> seleccionar(List<Cromosoma> poblacion) {
        List<Cromosoma> seleccionados = new ArrayList<>();
        Random random = new Random();

        int torneoSize = 5; // Tamaño del torneo

        for (int i = 0; i < poblacion.size(); i++) {
            Cromosoma mejor = null;

            for (int j = 0; j < torneoSize; j++) {
                Cromosoma competidor = poblacion.get(random.nextInt(poblacion.size()));
                if (mejor == null || competidor.getAptitud() > mejor.getAptitud()) {
                    mejor = competidor;
                }
            }

            seleccionados.add(mejor);
        }

        return seleccionados;
    }

    private List<Cromosoma> cruzar(List<Cromosoma> seleccionados) {
        List<Cromosoma> nuevaPoblacion = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < seleccionados.size() / 2; i++) {
            Cromosoma padre1 = seleccionados.get(random.nextInt(seleccionados.size()));
            Cromosoma padre2 = seleccionados.get(random.nextInt(seleccionados.size()));

            int puntoCruce = random.nextInt(padre1.getGenes().size());

            List<Gen> genesHijo1 = new ArrayList<>();
            List<Gen> genesHijo2 = new ArrayList<>();

            genesHijo1.addAll(padre1.getGenes().subList(0, puntoCruce));
            genesHijo1.addAll(padre2.getGenes().subList(puntoCruce, padre2.getGenes().size()));

            genesHijo2.addAll(padre2.getGenes().subList(0, puntoCruce));
            genesHijo2.addAll(padre1.getGenes().subList(puntoCruce, padre1.getGenes().size()));

            nuevaPoblacion.add(new Cromosoma(genesHijo1));
            nuevaPoblacion.add(new Cromosoma(genesHijo2));
        }

        return nuevaPoblacion;
    }

    private void mutar(Cromosoma cromosoma) {
        Random random = new Random();

        for (Gen gen : cromosoma.getGenes()) {
            if (random.nextDouble() < 0.1) { // Probabilidad de mutación del 10%
                gen.setDia(obtenerDiaAleatorio(cromosoma.getGenes(), gen.getGrupo()));
                gen.setHoraInicio(obtenerHoraInicioAleatoria(16));
                gen.setHoraFin(new Time(gen.getHoraInicio().getTime() + 2 * 60 * 60 * 1000)); // 2 horas adicionales
                gen.setAula(obtenerAulaAleatoria(gen.getGrupo().getAsignatura().getNecesitaComputadores()));
            }
        }
    }

    private Cromosoma encontrarMejorSolucion(List<Cromosoma> poblacion) {
        return poblacion.stream().max(Comparator.comparing(Cromosoma::getAptitud)).orElse(null);
    }

    private void guardarMejorSolucion(Cromosoma mejorSolucion) {
        for (Gen gen : mejorSolucion.getGenes()) {
            Horario horario = new Horario();
            horario.setGrupo(gen.getGrupo());
            horario.setProfesor(gen.getProfesor());
            horario.setAula(gen.getAula());
            horario.setDia(gen.getDia());
            horario.setHoraInicio(gen.getHoraInicio());
            horario.setHoraFin(gen.getHoraFin());

            // Agregar logs de verificación antes de guardar
            logs.add("Guardando en la base de datos: " + gen.getDia() + " desde " + gen.getHoraInicio() + " hasta " + gen.getHoraFin() + " para el grupo " + gen.getGrupo().getIdGrupo());

            horarioRepository.save(horario);
        }
    }


    // Métodos auxiliares para obtener datos aleatorios
    private Aula obtenerAulaAleatoria(boolean necesitaComputadores) {
        return aulaService.obtenerAulaAleatoria(necesitaComputadores);
    }

    private Time obtenerHoraInicioAleatoria(int maxHorasDisponibles) {
        Random random = new Random();
        int hora = 6 + random.nextInt(maxHorasDisponibles);  // Horas entre 6:00 AM y 10:00 PM (variable según el bloque)
        return Time.valueOf(String.format("%02d:00:00", hora)); // Retornar la hora en un formato de hora completa
    }

    public void mostrarLogs() {
        System.out.println("Logs del proceso:");
        logs.forEach(System.out::println);
    }
}
