package uis.horariouis.service;

import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.model.Aula;
import uis.horariouis.model.Horario;
import uis.horariouis.model.Grupo;
import uis.horariouis.model.Profesor;
import uis.horariouis.repository.GrupoRepository;
import uis.horariouis.repository.HorarioRepository;
import java.sql.Time;
import java.time.Instant;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class HorarioGen {

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private ProfesorService profesorService;

    @Autowired
    private AulaService aulaService;

    @Getter
    private final Random random = new Random();

    // Penalizaciones y recompensas
    private static final double PENALIZACION_SOLAPAMIENTO_AULA = 50.0;
    private static final double PENALIZACION_SOLAPAMIENTO_PROFESOR = 50.0;
    private static final double PENALIZACION_BLOQUE_MISMO_DIA = 20.0;
    private static final double PENALIZACION_AULA_INADECUADA = 100.0;
    private static final double PENALIZACION_PROFESOR_INADECUADO = 100.0;
    private static final double PENALIZACION_HORAS_NO_CORRECTAS = 30.0;

    private static final double RECOMPENSA_ASIGNACION_CORRECTA = 20.0;

    // Función de fitness que evalúa cada cromosoma
    private double fitness(Genotype<IntegerGene> gt, Grupo grupo, Profesor profesor) {
        int horasRequeridas = grupo.getAsignatura().getHorasTeoria();
        int horasAsignadas = 0;

        // Extraer los días y horas de los bloques
        int dia1 = gt.get(0).get(0).intValue();
        int horaInicio1 = gt.get(1).get(0).intValue();
        int dia2 = gt.get(2).get(0).intValue();
        int horaInicio2 = gt.get(3).get(0).intValue();

        // Comenzamos con un valor de fitness de 100
        double fitness = 100.0;

        // Penalizar si los bloques están en el mismo día
        if (dia1 == dia2) {
            fitness -= PENALIZACION_BLOQUE_MISMO_DIA;
        }

        // Obtener un aula adecuada para el grupo
        Aula aula = aulaService.obtenerAulaAdecuada(grupo.getCupo(), grupo.getAsignatura().getNecesitaComputadores());
        if (aula == null) {
            return -PENALIZACION_AULA_INADECUADA;
        }

        // Penalizar si hay solapamiento en los bloques de aulas
        if (aulaService.existeSolapamiento(aula, dia1, horaInicio1, horasRequeridas == 5 ? 3 : 2)) {
            fitness -= PENALIZACION_SOLAPAMIENTO_AULA;
        }
        if (aulaService.existeSolapamiento(aula, dia2, horaInicio2, 2)) {
            fitness -= PENALIZACION_SOLAPAMIENTO_AULA;
        }


        // Penalizar si el profesor no está relacionado con la asignatura del grupo
        if (!profesorService.esProfesorAdecuado(grupo, profesor)) {
            fitness -= PENALIZACION_PROFESOR_INADECUADO;
        }

        // Penalizar si hay solapamiento en los bloques de profesor
        if (profesorService.existeSolapamientoProfesor(profesor, dia1, horaInicio1, horasRequeridas == 5 ? 3 : 2)) {
            fitness -= PENALIZACION_SOLAPAMIENTO_PROFESOR;
        }
        if (profesorService.existeSolapamientoProfesor(profesor, dia2, horaInicio2, 2)) {
            fitness -= PENALIZACION_SOLAPAMIENTO_PROFESOR;
        }

        // Recompensar si los bloques se distribuyen correctamente en días distintos
        if (dia1 != dia2) {
            fitness += RECOMPENSA_ASIGNACION_CORRECTA;
        }

        // Ajuste para materias de 5 horas (3 + 2 horas)
        if (horasRequeridas == 5) {
            horasAsignadas += 3;
            if (dia1 != dia2) {
                horasAsignadas += 2;
            }
            if (horasAsignadas != 5) {
                fitness -= PENALIZACION_HORAS_NO_CORRECTAS;
            }
        }

        // Ajuste para materias de 4 horas (2 + 2 horas)
        if (horasRequeridas == 4) {
            horasAsignadas += 2;
            if (dia1 != dia2) {
                horasAsignadas += 2;
            }
            if (horasAsignadas != 4) {
                fitness -= PENALIZACION_HORAS_NO_CORRECTAS;
            }
        }

        return fitness;
    }

    // Método para generar el mejor horario usando Jenetics
    public void generarHorario() {
        Instant inicio = Instant.now(); // Capturar el tiempo de inicio
        List<Grupo> grupos = grupoRepository.findAll();

        // Aleatorizar (randomizar) la lista de grupos
        Collections.shuffle(grupos);

        for (Grupo grupo : grupos) {
            try {
                Profesor profesor = profesorService.obtenerProfesorAdecuado(grupo);

                if (profesor == null) {
                    System.out.println("Advertencia: No se encontró un profesor adecuado para el grupo "
                            + grupo.getNombreGrupo() + " de la asignatura " + grupo.getAsignatura().getNombre());
                    continue;
                }

                // Crear el genotipo para el grupo
                Genotype<IntegerGene> gtf = crearGenotipo(grupo);

                // Crear el motor de optimización
                Engine<IntegerGene, Double> engine = Engine
                        .builder(gt -> fitness(gt, grupo, profesor), gtf)
                        .populationSize(200)
                        .optimize(Optimize.MAXIMUM)
                        .alterers(new Mutator<>(0.1), new SinglePointCrossover<>(0.6))
                        .build();


                // Ejecutar el algoritmo genético
                Genotype<IntegerGene> result = engine.stream()
                        .limit(100)
                        .collect(EvolutionResult.toBestGenotype());

                // Guardar los horarios resultantes
                guardarHorario(result, grupo, profesor);

            } catch (Exception e) {
                System.err.println("Error al generar horario para el grupo " + grupo.getNombreGrupo()
                        + " de la asignatura " + grupo.getAsignatura().getNombre() + ": " + e.getMessage());
            }
        }
        Instant fin = Instant.now(); // Capturar el tiempo de finalización
        Duration duracion = Duration.between(inicio, fin); // Calcular la duración

        long minutos = duracion.toMinutes();
        long segundos = duracion.getSeconds() % 60;
        long milisegundos = duracion.toMillis() % 1000;

        System.out.printf("Tiempo total de ejecución: %d minutos, %d segundos, %d ms%n", minutos, segundos, milisegundos);
    }

    // Método separado para crear el genotipo del grupo
    private Genotype<IntegerGene> crearGenotipo(Grupo grupo) {
        int horasRequeridas = grupo.getAsignatura().getHorasTeoria();

        if (horasRequeridas == 5) {
            // Asignaturas de 5 horas (3 + 2 horas)
            return Genotype.of(
                    IntegerChromosome.of(1, 6),  // Día del primer bloque
                    IntegerChromosome.of(6, 19), // Hora inicio del primer bloque (3 horas)
                    IntegerChromosome.of(1, 6),  // Día del segundo bloque
                    IntegerChromosome.of(6, 20)  // Hora inicio del segundo bloque (2 horas)
            );
        } else if (horasRequeridas == 4) {
            // Asignaturas de 4 horas (2 + 2 horas)
            return Genotype.of(
                    IntegerChromosome.of(1, 6),  // Día del primer bloque
                    IntegerChromosome.of(6, 20), // Hora inicio del primer bloque (2 horas)
                    IntegerChromosome.of(1, 6),  // Día del segundo bloque
                    IntegerChromosome.of(6, 20)  // Hora inicio del segundo bloque (2 horas)
            );
        }

        throw new IllegalArgumentException("Horas requeridas no válidas para la asignatura: "
                + grupo.getAsignatura().getNombre());
    }

    private void guardarHorario(Genotype<IntegerGene> result, Grupo grupo, Profesor profesor) {
        int dia1 = result.get(0).get(0).intValue();
        int horaInicio1 = result.get(1).get(0).intValue();
        int dia2 = result.get(2).get(0).intValue();
        int horaInicio2 = result.get(3).get(0).intValue();

        int horasDuracion1 = grupo.getAsignatura().getHorasTeoria() == 5 ? 3 : 2;
        int horasDuracion2 = 2;

        Aula aula = aulaService.obtenerAulaAdecuada(grupo.getCupo(), grupo.getAsignatura().getNecesitaComputadores());

        // Reintentar el primer bloque si hay solapamiento de aula o profesor
        aula = aulaService.reintentarSolapamientoAula(aula, dia1, horaInicio1, horasDuracion1, grupo);
        profesor = profesorService.reintentarSolapamientoProfesor(profesor, dia1, horaInicio1, horasDuracion1, grupo);

        if (aula == null || profesor == null) {
            throw new RuntimeException("No se pudo encontrar un aula o profesor disponible para el primer bloque");
        }

        Horario horario1 = new Horario(null, profesor, grupo, aula, aulaService.convertirDia(dia1),
                Time.valueOf(horaInicio1 + ":00:00"), Time.valueOf((horaInicio1 + horasDuracion1) + ":00:00"));
        horarioRepository.save(horario1);

        // Reintentar el segundo bloque si hay solapamiento de aula o profesor
        aula = aulaService.reintentarSolapamientoAula(aula, dia2, horaInicio2, horasDuracion2, grupo);
        profesor = profesorService.reintentarSolapamientoProfesor(profesor, dia2, horaInicio2, horasDuracion2, grupo);

        if (aula == null || profesor == null) {
            throw new RuntimeException("No se pudo encontrar un aula o profesor disponible para el segundo bloque");
        }

        Horario horario2 = new Horario(null, profesor, grupo, aula, aulaService.convertirDia(dia2),
                Time.valueOf(horaInicio2 + ":00:00"), Time.valueOf((horaInicio2 + horasDuracion2) + ":00:00"));
        horarioRepository.save(horario2);
    }

}
