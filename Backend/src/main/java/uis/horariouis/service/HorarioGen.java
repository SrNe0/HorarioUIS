package uis.horariouis.service;

import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.model.Horario;
import uis.horariouis.model.Aula;
import uis.horariouis.model.Grupo;
import uis.horariouis.model.Profesor;
import uis.horariouis.repository.AulaRepository;
import uis.horariouis.repository.GrupoRepository;
import uis.horariouis.repository.HorarioRepository;
import uis.horariouis.repository.ProfesorRepository;

import java.sql.Time;
import java.util.List;
import java.util.Random;

@Service
public class HorarioGen {

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private ProfesorRepository profesorRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private AulaRepository aulaRepository;

    private final Random random = new Random();

    // Función de fitness que evalúa cada cromosoma
    private double fitness(Genotype<IntegerGene> gt, Grupo grupo) {
        int horasRequeridas = grupo.getAsignatura().getHorasTeoria();  // Obtener horas de teoría desde la asignatura
        int horasAsignadas = 0;

        // Extraer los días y horas de los bloques
        int dia1 = gt.get(0).get(0).intValue();
        int horaInicio1 = gt.get(1).get(0).intValue();  // Usaremos esta variable más adelante
        int dia2 = gt.get(2).get(0).intValue();
        int horaInicio2 = gt.get(3).get(0).intValue();  // Usaremos esta variable más adelante

        // Penalizar si los bloques están en el mismo día
        double fitness = 100.0;
        if (dia1 == dia2) {
            fitness -= 30;  // Penaliza si los dos bloques están en el mismo día
        }

        // Sumar horas asignadas (3 horas en el primer bloque, 2 horas en el segundo bloque)
        horasAsignadas += 3;
        if (dia1 != dia2) {
            horasAsignadas += 2;  // Agregar 2 horas adicionales si están en días distintos
        }

        // Penalizar si no se asignan exactamente 5 horas
        if (horasAsignadas != horasRequeridas) {
            fitness -= 50;  // Penaliza fuertemente si no se asignan exactamente 5 horas
        }

        return fitness;
    }

    // Método para generar el mejor horario usando Jenetics
    public void generarHorario() {
        // Obtener solo los grupos que tienen asignaturas con 5 horas de teoría
        List<Grupo> grupos = grupoRepository.findAll();
        for (Grupo grupo : grupos) {
            if (grupo.getAsignatura().getHorasTeoria() == 5) {
                // Crear el Genotipo que maneje las asignaturas de 5 horas
                Genotype<IntegerGene> gtf = Genotype.of(
                        // Asignatura de 5 horas (1 bloque de 3 horas y 1 bloque de 2 horas)
                        IntegerChromosome.of(1, 6),  // Día del primer bloque
                        IntegerChromosome.of(6, 20),  // Hora de inicio del bloque de 3 horas
                        IntegerChromosome.of(1, 6),  // Día del segundo bloque
                        IntegerChromosome.of(6, 20)   // Hora de inicio del bloque de 2 horas
                );

                // Crear el motor de optimización
                Engine<IntegerGene, Double> engine = Engine
                        .builder(gt -> fitness(gt, grupo), gtf)
                        .populationSize(200)
                        .optimize(Optimize.MAXIMUM)
                        .alterers(new Mutator<>(0.1), new SinglePointCrossover<>(0.6))
                        .build();

                // Ejecutar el algoritmo genético para obtener la mejor solución
                Genotype<IntegerGene> result = engine.stream()
                        .limit(100)
                        .collect(EvolutionResult.toBestGenotype());

                // Convertir el resultado en horarios y guardarlos en la base de datos
                guardarHorario(result, grupo);
            }
        }
    }

    // Método para seleccionar aleatoriamente un profesor y aula
    private Profesor obtenerProfesorAleatorio() {
        List<Profesor> profesores = profesorRepository.findAll();
        return profesores.get(random.nextInt(profesores.size()));
    }

    private Aula obtenerAulaAleatoria() {
        List<Aula> aulas = aulaRepository.findAll();
        return aulas.get(random.nextInt(aulas.size()));
    }

    // Método para convertir el resultado en horarios y guardarlos en la base de datos
    private void guardarHorario(Genotype<IntegerGene> result, Grupo grupo) {
        // Extraer los días y horas de los bloques
        int dia1 = result.get(0).get(0).intValue();
        int horaInicio1 = result.get(1).get(0).intValue();
        int dia2 = result.get(2).get(0).intValue();
        int horaInicio2 = result.get(3).get(0).intValue();

        // Obtener valores aleatorios de la base de datos para profesor y aula
        Profesor profesor = obtenerProfesorAleatorio();
        Aula aula = obtenerAulaAleatoria();

        // Guardar los horarios generados (primer bloque de 3 horas, segundo bloque de 2 horas)
        Horario horario1 = new Horario(null, profesor, grupo, aula, convertirDia(dia1), Time.valueOf(horaInicio1 + ":00:00"), Time.valueOf((horaInicio1 + 3) + ":00:00"));
        horarioRepository.save(horario1);

        Horario horario2 = new Horario(null, profesor, grupo, aula, convertirDia(dia2), Time.valueOf(horaInicio2 + ":00:00"), Time.valueOf((horaInicio2 + 2) + ":00:00"));
        horarioRepository.save(horario2);
    }

    // Método para convertir el número de día en un nombre de día (1 = Lunes, 6 = Sábado)
    private String convertirDia(int dia) {
        return switch (dia) {
            case 1 -> "Lunes";
            case 2 -> "Martes";
            case 3 -> "Miércoles";
            case 4 -> "Jueves";
            case 5 -> "Viernes";
            case 6 -> "Sábado";
            default -> "Desconocido";
        };
    }
}
