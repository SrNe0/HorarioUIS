package uis.horariouis.service;

import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.model.Aula;
import uis.horariouis.model.Horario;
import uis.horariouis.model.Grupo;
import uis.horariouis.model.Profesor;
import uis.horariouis.repository.GrupoRepository;
import uis.horariouis.repository.HorarioRepository;
import java.sql.Time;
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

    private final Random random = new Random();

    // Función de fitness que evalúa cada cromosoma
    private double fitness(Genotype<IntegerGene> gt, Grupo grupo, Profesor profesor) {
        int horasRequeridas = grupo.getAsignatura().getHorasTeoria();
        int horasAsignadas = 0;

        // Extraer los días y horas de los bloques
        int dia1 = gt.get(0).get(0).intValue();
        int horaInicio1 = gt.get(1).get(0).intValue();
        int dia2 = gt.get(2).get(0).intValue();
        int horaInicio2 = gt.get(3).get(0).intValue();

        // Penalizar si los bloques están en el mismo día
        double fitness = 100.0;
        if (dia1 == dia2) {
            fitness -= 30;  // Penaliza si los dos bloques están en el mismo día
        }

        // Obtener un aula adecuada para el grupo
        Aula aula = aulaService.obtenerAulaAdecuada(grupo.getCupo(), grupo.getAsignatura().getNecesitaComputadores());

        if (aula == null) {
            return -100.0;  // Penalización máxima si no hay un aula adecuada
        }

        // Penalizar si hay solapamiento en los bloques
        if (aulaService.existeSolapamiento(aula, dia1, horaInicio1, horasRequeridas == 5 ? 3 : 2)
                || aulaService.existeSolapamiento(aula, dia2, horaInicio2, 2)) {
            fitness -= 100;  // Penalización fuerte por solapamiento
        }

        // Penalizar si el profesor no está relacionado con la asignatura del grupo
        if (!profesorService.esProfesorAdecuado(grupo, profesor)) {
            fitness -= 100;
        }

        // Ajuste para materias de 5 horas (3 + 2 horas)
        if (horasRequeridas == 5) {
            horasAsignadas += 3;
            if (dia1 != dia2) {
                horasAsignadas += 2;
            }
            if (horasAsignadas != 5) {
                fitness -= 50;
            }
        }

        // Ajuste para materias de 4 horas (2 + 2 horas)
        if (horasRequeridas == 4) {
            horasAsignadas += 2;
            if (dia1 != dia2) {
                horasAsignadas += 2;
            }
            if (horasAsignadas != 4) {
                fitness -= 50;
            }
        }

        return fitness;
    }

    // Método para generar el mejor horario usando Jenetics
    public void generarHorario() {
        List<Grupo> grupos = grupoRepository.findAll();

        for (Grupo grupo : grupos) {
            Profesor profesor = profesorService.obtenerProfesorAdecuado(grupo);

            if (profesor == null) {
                System.out.println("Excluyendo grupo " + grupo.getNombreGrupo() + " de la asignatura "
                        + grupo.getAsignatura().getNombre() + " porque no tiene profesor asignado.");
                continue;
            }

            Genotype<IntegerGene> gtf = Genotype.of(
                    IntegerChromosome.of(1, 6),  // Día del primer bloque
                    IntegerChromosome.of(6, 20), // Hora inicio del primer bloque
                    IntegerChromosome.of(1, 6),  // Día del segundo bloque
                    IntegerChromosome.of(6, 20)  // Hora inicio del segundo bloque
            );

            Engine<IntegerGene, Double> engine = Engine
                    .builder(gt -> fitness(gt, grupo, profesor), gtf)
                    .populationSize(200)
                    .optimize(Optimize.MAXIMUM)
                    .alterers(new Mutator<>(0.1), new SinglePointCrossover<>(0.6))
                    .build();

            Genotype<IntegerGene> result = engine.stream()
                    .limit(100)
                    .collect(EvolutionResult.toBestGenotype());

            guardarHorario(result, grupo, profesor);
        }
    }

    private void guardarHorario(Genotype<IntegerGene> result, Grupo grupo, Profesor profesor) {
        int dia1 = result.get(0).get(0).intValue();
        int horaInicio1 = result.get(1).get(0).intValue();
        int dia2 = result.get(2).get(0).intValue();
        int horaInicio2 = result.get(3).get(0).intValue();

        int horasDuracion1 = grupo.getAsignatura().getHorasTeoria() == 5 ? 3 : 2;
        int horasDuracion2 = 2;

        Aula aula = aulaService.obtenerAulaAdecuada(grupo.getCupo(), grupo.getAsignatura().getNecesitaComputadores());


        // Reintentar el primer bloque si hay solapamiento
        aula = reintentarSolapamiento(aula, dia1, horaInicio1, horasDuracion1, grupo);
        if (aula == null) {
            throw new RuntimeException("No se pudo encontrar un aula disponible para el primer bloque");
        }

        Horario horario1 = new Horario(null, profesor, grupo, aula, aulaService.convertirDia(dia1),
                Time.valueOf(horaInicio1 + ":00:00"), Time.valueOf((horaInicio1 + horasDuracion1) + ":00:00"));
        horarioRepository.save(horario1);

        // Reintentar el segundo bloque si hay solapamiento
        aula = reintentarSolapamiento(aula, dia2, horaInicio2, horasDuracion2, grupo);
        if (aula == null) {
            throw new RuntimeException("No se pudo encontrar un aula disponible para el segundo bloque");
        }

        Horario horario2 = new Horario(null, profesor, grupo, aula, aulaService.convertirDia(dia2),
                Time.valueOf(horaInicio2 + ":00:00"), Time.valueOf((horaInicio2 + horasDuracion2) + ":00:00"));
        horarioRepository.save(horario2);
    }

    // Método para reintentar el solapamiento cambiando aula, día o hora
    private Aula reintentarSolapamiento(Aula aulaInicial, int dia, int horaInicio, int horasDuracion, Grupo grupo) {
        Aula aula = aulaInicial;
        int intentos = 0;

        // Intentar hasta 10 veces cambiar aula o reprogramar hora
        while (aulaService.existeSolapamiento(aula, dia, horaInicio, horasDuracion) && intentos < 10) {
            aula = aulaService.obtenerAulaAdecuada(grupo.getCupo(), grupo.getAsignatura().getNecesitaComputadores());


            // Cambiar a otro día y hora si hay solapamiento
            dia = random.nextInt(6) + 1;  // Cambiar a un día diferente (1 a 6, lunes a sábado)
            horaInicio = random.nextInt(14) + 6;  // Cambiar a una nueva hora (de 6 a 20)

            intentos++;
        }

        // Si después de 10 intentos no se encuentra una solución, devolver null
        if (aulaService.existeSolapamiento(aula, dia, horaInicio, horasDuracion)) {
            return null;
        }

        return aula;
    }

}
