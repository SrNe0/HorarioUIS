package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.model.*;
import uis.horariouis.repository.HorarioRepository;
import uis.horariouis.verification.ReparacionAlgoritmoGenetico;
import uis.horariouis.verification.RestriccionesAlgoritmoGenetico;

import java.sql.Time;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class AlgoritmoGeneticoService {

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private AulaService aulaService;

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private AjusteHorasService ajusteHorasService;

    @Autowired
    private RestriccionesAlgoritmoGenetico restriccionesAlgoritmoGenetico;


    @Autowired
    private ReparacionAlgoritmoGenetico reparacionAlgoritmoGenetico;

    private final List<String> logs = new ArrayList<>();

    public void ejecutarAlgoritmoGenetico() {
        asignarYGuardarBloquesDe5Horas();
        asignarYGuardarBloquesDe4Horas();
        ajusteHorasService.ajustarHorariosDe5Horas();
        mostrarLogs();
    }

    private void asignarYGuardarBloquesDe5Horas() {
        List<Grupo> grupos5Horas = grupoService.obtenerGruposConProfesor().stream()
                .filter(grupo -> Optional.ofNullable(grupo.getAsignatura())
                        .map(asignatura -> asignatura.getHorasTeoria() == 5)
                        .orElse(false))
                .collect(Collectors.toList());

        List<Cromosoma> poblacion5Horas = inicializarPoblacion5Horas(grupos5Horas);

        for (int i = 0; i < 400; i++) {
            poblacion5Horas.forEach(this::calcularAptitud);
            List<Cromosoma> seleccionados = seleccionar(poblacion5Horas);
            List<Cromosoma> nuevaPoblacion = cruzar(seleccionados);
            nuevaPoblacion.forEach(this::mutar);
            poblacion5Horas = nuevaPoblacion;
        }

        guardarMejorSolucion(encontrarMejorSolucion(poblacion5Horas));
    }

    private void asignarYGuardarBloquesDe4Horas() {
        List<Grupo> grupos4Horas = grupoService.obtenerGruposConProfesor().stream()
                .filter(grupo -> Optional.ofNullable(grupo.getAsignatura())
                        .map(asignatura -> asignatura.getHorasTeoria() == 4)
                        .orElse(false))
                .collect(Collectors.toList());

        List<Cromosoma> poblacion4Horas = inicializarPoblacion4Horas(grupos4Horas);

        for (int i = 0; i < 400; i++) {
            poblacion4Horas.forEach(this::calcularAptitud);
            List<Cromosoma> seleccionados = seleccionar(poblacion4Horas);
            List<Cromosoma> nuevaPoblacion = cruzar(seleccionados);
            nuevaPoblacion.forEach(this::mutar);
            poblacion4Horas = nuevaPoblacion;
        }

        guardarMejorSolucion(encontrarMejorSolucion(poblacion4Horas));
    }

    private List<Cromosoma> inicializarPoblacion5Horas(List<Grupo> grupos) {
        return IntStream.range(0, 200)
                .mapToObj(i -> {
                    List<Gen> genes = grupos.stream()
                            .flatMap(grupo -> crearGenesParaGrupo(grupo).stream())
                            .collect(Collectors.toList());

                    logs.add("Cromosoma " + i + " generado con " + genes.size() + " genes.");
                    return new Cromosoma(genes);
                })
                .collect(Collectors.toList());
    }

    private List<Cromosoma> inicializarPoblacion4Horas(List<Grupo> grupos) {
        return IntStream.range(0, 100)
                .mapToObj(i -> {
                    List<Gen> genes = grupos.stream()
                            .flatMap(grupo -> IntStream.range(0, 2)
                                    .mapToObj(j -> crearGenParaBloque(grupo))
                                    .toList().stream())
                            .collect(Collectors.toList());

                    logs.add("Cromosoma " + i + " generado con " + genes.size() + " genes.");
                    return new Cromosoma(genes);
                })
                .collect(Collectors.toList());
    }

    private List<Gen> crearGenesParaGrupo(Grupo grupo) {
        List<Gen> genes = new ArrayList<>();
        Profesor profesor = grupo.getAsignatura().getDictados().stream().findFirst().orElseThrow().getProfesor();
        Aula aula = obtenerAulaAleatoria(grupo.getAsignatura().getNecesitaComputadores());

        Gen gen1 = crearGenParaBloque(grupo, profesor, aula, 14);
        genes.add(gen1);

        Gen gen2;
        do {
            gen2 = crearGenParaBloque(grupo, profesor, aula, 14);
        } while (gen2.getDia().equals(gen1.getDia()));

        genes.add(gen2);

        Gen gen3;
        do {
            gen3 = crearGenParaBloque(grupo, profesor, aula, 21, 1);
        } while (gen3.getDia().equals(gen1.getDia()) || gen3.getDia().equals(gen2.getDia()));

        genes.add(gen3);

        return genes;
    }

    private Gen crearGenParaBloque(Grupo grupo, Profesor profesor, Aula aula, int maxHoraInicio) {
        String dia = obtenerDiaAleatorio(new ArrayList<>(), grupo);
        Time horaInicio = obtenerHoraInicioAleatoria(maxHoraInicio);
        Time horaFin = Time.valueOf(String.format("%02d:00:00", horaInicio.toLocalTime().getHour() + 2));
        logs.add("Asignado bloque de 2 horas: Inicio " + horaInicio + ", Fin: " + horaFin + " al grupo " + grupo.getIdGrupo());
        return new Gen(grupo, profesor, aula, dia, horaInicio, horaFin);
    }

    private Gen crearGenParaBloque(Grupo grupo, Profesor profesor, Aula aula, int maxHoraInicio, int duracionHoras) {
        String dia = obtenerDiaAleatorio(new ArrayList<>(), grupo);
        Time horaInicio = obtenerHoraInicioAleatoria(maxHoraInicio);
        Time horaFin = Time.valueOf(String.format("%02d:00:00", horaInicio.toLocalTime().getHour() + duracionHoras));
        logs.add("Asignado bloque de " + duracionHoras + " horas: Inicio " + horaInicio + ", Fin: " + horaFin + " al grupo " + grupo.getIdGrupo());
        return new Gen(grupo, profesor, aula, dia, horaInicio, horaFin);
    }

    private Gen crearGenParaBloque(Grupo grupo) {
        Profesor profesor = grupo.getAsignatura().getDictados().stream().findFirst().orElseThrow().getProfesor();
        Aula aula = obtenerAulaAleatoria(grupo.getAsignatura().getNecesitaComputadores());
        return crearGenParaBloque(grupo, profesor, aula, 16);
    }

    private void calcularAptitud(Cromosoma cromosoma) {

        int aptitud = restriccionesAlgoritmoGenetico.calcularAptitud(cromosoma);
        cromosoma.setAptitud(aptitud);
    }

    private List<Cromosoma> seleccionar(List<Cromosoma> poblacion) {
        Random random = new Random();
        return IntStream.range(0, poblacion.size())
                .mapToObj(i -> IntStream.range(0, 5)
                        .mapToObj(j -> poblacion.get(random.nextInt(poblacion.size())))
                        .max(Comparator.comparing(Cromosoma::getAptitud))
                        .orElseThrow())
                .collect(Collectors.toList());
    }

    private List<Cromosoma> cruzar(List<Cromosoma> seleccionados) {
        Random random = new Random();
        return IntStream.range(0, seleccionados.size() / 2)
                .mapToObj(i -> {
                    Cromosoma padre1 = seleccionados.get(random.nextInt(seleccionados.size()));
                    Cromosoma padre2 = seleccionados.get(random.nextInt(seleccionados.size()));
                    int puntoCruce = random.nextInt(padre1.getGenes().size());

                    List<Gen> genesHijo1 = new ArrayList<>(padre1.getGenes().subList(0, puntoCruce));
                    genesHijo1.addAll(padre2.getGenes().subList(puntoCruce, padre2.getGenes().size()));

                    List<Gen> genesHijo2 = new ArrayList<>(padre2.getGenes().subList(0, puntoCruce));
                    genesHijo2.addAll(padre1.getGenes().subList(puntoCruce, padre1.getGenes().size()));

                    return Arrays.asList(new Cromosoma(genesHijo1), new Cromosoma(genesHijo2));
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private void mutar(Cromosoma cromosoma) {
        Random random = new Random();
        cromosoma.getGenes().forEach(gen -> {
            if (random.nextDouble() < 0.1) { // Probabilidad de mutación del 10%
                gen.setDia(obtenerDiaAleatorio(cromosoma.getGenes(), gen.getGrupo()));
                gen.setHoraInicio(obtenerHoraInicioAleatoria(16));
                gen.setHoraFin(new Time(gen.getHoraInicio().getTime() + 2 * 60 * 60 * 1000)); // 2 horas adicionales
                gen.setAula(obtenerAulaAleatoria(gen.getGrupo().getAsignatura().getNecesitaComputadores()));
            }
        });
    }

    private Cromosoma encontrarMejorSolucion(List<Cromosoma> poblacion) {
        return poblacion.stream()
                .max(Comparator.comparing(Cromosoma::getAptitud))
                .orElse(null);
    }

    private void guardarMejorSolucion(Cromosoma mejorSolucion) {
        StringBuilder errores = new StringBuilder();

        // Verificar restricciones y aplicar reparaciones si es necesario
        if (restriccionesAlgoritmoGenetico.haySolapamientoAula(mejorSolucion)) {
            errores.append("Solapamiento de aulas detectado. Intentando corregir...\n");
            reparacionAlgoritmoGenetico.corregirSolapamientoAula(mejorSolucion.getGenes());
        }

        if (restriccionesAlgoritmoGenetico.haySolapamientoProfesor(mejorSolucion)) {
            errores.append("Solapamiento de profesores detectado. Intentando corregir...\n");
            reparacionAlgoritmoGenetico.corregirSolapamientoProfesor(mejorSolucion.getGenes());
        }

        // Verificar nuevamente después de la corrección
        boolean solapamientoAulaPostCorreccion = restriccionesAlgoritmoGenetico.haySolapamientoAula(mejorSolucion);
        boolean solapamientoProfesorPostCorreccion = restriccionesAlgoritmoGenetico.haySolapamientoProfesor(mejorSolucion);

        if (!solapamientoAulaPostCorreccion && !solapamientoProfesorPostCorreccion) {
            // No hay solapamientos, proceder a guardar
            mejorSolucion.getGenes().forEach(gen -> {
                Horario horario = crearHorarioDesdeGen(gen);
                horarioRepository.save(horario);
            });
            logs.add("Horarios guardados exitosamente después de las correcciones.");
        } else {
            // Si los errores persisten después de las correcciones, registrar en los logs
            if (solapamientoAulaPostCorreccion) {
                errores.append("Error: Solapamiento de aulas persistente después de la corrección.\n");
            }
            if (solapamientoProfesorPostCorreccion) {
                errores.append("Error: Solapamiento de profesores persistente después de la corrección.\n");
            }
            logs.add(errores.toString());
        }
    }

    private Horario crearHorarioDesdeGen(Gen gen) {
        Horario horario = new Horario();
        horario.setGrupo(gen.getGrupo());
        horario.setProfesor(gen.getProfesor());
        horario.setAula(gen.getAula());
        horario.setDia(gen.getDia());
        horario.setHoraInicio(gen.getHoraInicio());
        horario.setHoraFin(gen.getHoraFin());
        return horario;
    }

    // Métodos auxiliares para obtener datos aleatorios
    private Aula obtenerAulaAleatoria(boolean necesitaComputadores) {
        return aulaService.obtenerAulaAleatoria(necesitaComputadores);
    }

    private Time obtenerHoraInicioAleatoria(int maxHorasDisponibles) {
        int hora = 6 + new Random().nextInt(maxHorasDisponibles);
        return Time.valueOf(String.format("%02d:00:00", hora));
    }

    private String obtenerDiaAleatorio(List<Gen> genes, Grupo grupoActual) {
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"};
        List<String> diasDisponibles = Arrays.stream(dias)
                .filter(dia -> genes.stream().noneMatch(gen -> gen.getGrupo().equals(grupoActual) && gen.getDia().equals(dia)))
                .toList();

        return diasDisponibles.isEmpty() ? dias[new Random().nextInt(dias.length)] : diasDisponibles.get(new Random().nextInt(diasDisponibles.size()));
    }

    public void mostrarLogs() {
        logs.forEach(System.out::println);
    }
}
