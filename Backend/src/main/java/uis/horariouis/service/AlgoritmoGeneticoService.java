package uis.horariouis.service;

// Importación de clases necesarias para el funcionamiento del servicio
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

@Service // Indica que esta clase es un servicio gestionado por Spring
public class AlgoritmoGeneticoService {

    // Inyección de dependencias necesarias para el servicio
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

    // Lista para almacenar logs generados durante la ejecución del algoritmo
    private final List<String> logs = new ArrayList<>();

    // Método principal que ejecuta el algoritmo genético para asignar horarios
    public void ejecutarAlgoritmoGenetico() {
        // Asignar y guardar los bloques de 5 horas
        asignarYGuardarBloquesDe5Horas();
        // Asignar y guardar los bloques de 4 horas
        asignarYGuardarBloquesDe4Horas();
        // Ajustar los horarios de 5 horas según sea necesario
        ajusteHorasService.ajustarHorariosDe5Horas();
        // Mostrar los logs generados durante la ejecución
        mostrarLogs();
    }

    // Método que asigna y guarda los bloques de 5 horas para cada grupo
    private void asignarYGuardarBloquesDe5Horas() {
        // Obtener todos los grupos que tienen asignaturas con 5 horas de teoría
        List<Grupo> grupos5Horas = grupoService.obtenerGruposConProfesor().stream()
                .filter(grupo -> Optional.ofNullable(grupo.getAsignatura())
                        .map(asignatura -> asignatura.getHorasTeoria() == 5)
                        .orElse(false))
                .collect(Collectors.toList());

        // Inicializar la población de cromosomas (posibles soluciones) para los grupos de 5 horas
        List<Cromosoma> poblacion5Horas = inicializarPoblacion5Horas(grupos5Horas);

        // Realizar 400 iteraciones del algoritmo genético
        for (int i = 0; i < 400; i++) {
            // Calcular la aptitud de cada cromosoma
            poblacion5Horas.forEach(this::calcularAptitud);
            // Seleccionar los cromosomas más aptos para la siguiente generación
            List<Cromosoma> seleccionados = seleccionar(poblacion5Horas);
            // Aplicar cruce para generar nuevos cromosomas
            List<Cromosoma> nuevaPoblacion = cruzar(seleccionados);
            // Aplicar mutaciones aleatorias a los cromosomas
            nuevaPoblacion.forEach(this::mutar);
            // Reemplazar la población antigua con la nueva
            poblacion5Horas = nuevaPoblacion;
        }

        // Guardar la mejor solución encontrada en la base de datos
        guardarMejorSolucion(encontrarMejorSolucion(poblacion5Horas));
    }

    // Método que asigna y guarda los bloques de 4 horas para cada grupo
    private void asignarYGuardarBloquesDe4Horas() {
        // Obtener todos los grupos que tienen asignaturas con 4 horas de teoría
        List<Grupo> grupos4Horas = grupoService.obtenerGruposConProfesor().stream()
                .filter(grupo -> Optional.ofNullable(grupo.getAsignatura())
                        .map(asignatura -> asignatura.getHorasTeoria() == 4)
                        .orElse(false))
                .collect(Collectors.toList());

        // Inicializar la población de cromosomas (posibles soluciones) para los grupos de 4 horas
        List<Cromosoma> poblacion4Horas = inicializarPoblacion4Horas(grupos4Horas);

        // Realizar 400 iteraciones del algoritmo genético
        for (int i = 0; i < 400; i++) {
            // Calcular la aptitud de cada cromosoma
            poblacion4Horas.forEach(this::calcularAptitud);
            // Seleccionar los cromosomas más aptos para la siguiente generación
            List<Cromosoma> seleccionados = seleccionar(poblacion4Horas);
            // Aplicar cruce para generar nuevos cromosomas
            List<Cromosoma> nuevaPoblacion = cruzar(seleccionados);
            // Aplicar mutaciones aleatorias a los cromosomas
            nuevaPoblacion.forEach(this::mutar);
            // Reemplazar la población antigua con la nueva
            poblacion4Horas = nuevaPoblacion;
        }

        // Guardar la mejor solución encontrada en la base de datos
        guardarMejorSolucion(encontrarMejorSolucion(poblacion4Horas));
    }

    // Método que inicializa la población de cromosomas para los grupos de 5 horas
    private List<Cromosoma> inicializarPoblacion5Horas(List<Grupo> grupos) {
        return IntStream.range(0, 200) // Generar 200 cromosomas
                .mapToObj(i -> {
                    // Crear los genes para cada grupo
                    List<Gen> genes = grupos.stream()
                            .flatMap(grupo -> crearGenesParaGrupo(grupo).stream())
                            .collect(Collectors.toList());

                    logs.add("Cromosoma " + i + " generado con " + genes.size() + " genes.");
                    return new Cromosoma(genes); // Crear un cromosoma con los genes generados
                })
                .collect(Collectors.toList()); // Convertir el stream en una lista de cromosomas
    }

    // Método que inicializa la población de cromosomas para los grupos de 4 horas
    private List<Cromosoma> inicializarPoblacion4Horas(List<Grupo> grupos) {
        return IntStream.range(0, 100) // Generar 100 cromosomas
                .mapToObj(i -> {
                    // Crear dos genes para cada grupo
                    List<Gen> genes = grupos.stream()
                            .flatMap(grupo -> IntStream.range(0, 2)
                                    .mapToObj(j -> crearGenParaBloque(grupo))
                                    .toList().stream())
                            .collect(Collectors.toList());

                    logs.add("Cromosoma " + i + " generado con " + genes.size() + " genes.");
                    return new Cromosoma(genes); // Crear un cromosoma con los genes generados
                })
                .collect(Collectors.toList()); // Convertir el stream en una lista de cromosomas
    }

    // Método que crea los genes para un grupo específico
    private List<Gen> crearGenesParaGrupo(Grupo grupo) {
        List<Gen> genes = new ArrayList<>();
        // Obtener el profesor asignado a la asignatura del grupo
        Profesor profesor = grupo.getAsignatura().getDictados().stream().findFirst().orElseThrow().getProfesor();
        // Obtener un aula aleatoria que cumpla con los requisitos del grupo
        Aula aula = obtenerAulaAleatoria(grupo.getAsignatura().getNecesitaComputadores());

        // Crear el primer gen (bloque de 2 horas)
        Gen gen1 = crearGenParaBloque(grupo, profesor, aula, 14);
        genes.add(gen1);

        // Crear el segundo gen (otro bloque de 2 horas) asegurando que no sea el mismo día que el primero
        Gen gen2;
        do {
            gen2 = crearGenParaBloque(grupo, profesor, aula, 14);
        } while (gen2.getDia().equals(gen1.getDia()));

        genes.add(gen2);

        // Crear el tercer gen (bloque de 1 hora) asegurando que no sea el mismo día que los anteriores
        Gen gen3;
        do {
            gen3 = crearGenParaBloque(grupo, profesor, aula, 21, 1);
        } while (gen3.getDia().equals(gen1.getDia()) || gen3.getDia().equals(gen2.getDia()));

        genes.add(gen3);

        return genes; // Devolver la lista de genes creados
    }

    // Método que crea un gen representando un bloque de horas para un grupo en un día y hora específicos
    private Gen crearGenParaBloque(Grupo grupo, Profesor profesor, Aula aula, int maxHoraInicio) {
        // Obtener un día aleatorio para el bloque
        String dia = obtenerDiaAleatorio(new ArrayList<>(), grupo);
        // Obtener una hora de inicio aleatoria dentro del rango permitido
        Time horaInicio = obtenerHoraInicioAleatoria(maxHoraInicio);
        // Calcular la hora de fin sumando 2 horas a la hora de inicio
        Time horaFin = Time.valueOf(String.format("%02d:00:00", horaInicio.toLocalTime().getHour() + 2));
        logs.add("Asignado bloque de 2 horas: Inicio " + horaInicio + ", Fin: " + horaFin + " al grupo " + grupo.getIdGrupo());
        return new Gen(grupo, profesor, aula, dia, horaInicio, horaFin); // Crear y devolver el gen
    }

    // Sobrecarga del método anterior para crear un bloque de duración específica
    private Gen crearGenParaBloque(Grupo grupo, Profesor profesor, Aula aula, int maxHoraInicio, int duracionHoras) {
        // Obtener un día aleatorio para el bloque
        String dia = obtenerDiaAleatorio(new ArrayList<>(), grupo);
        // Obtener una hora de inicio aleatoria dentro del rango permitido
        Time horaInicio = obtenerHoraInicioAleatoria(maxHoraInicio);
        // Calcular la hora de fin sumando la duración específica a la hora de inicio
        Time horaFin = Time.valueOf(String.format("%02d:00:00", horaInicio.toLocalTime().getHour() + duracionHoras));
        logs.add("Asignado bloque de " + duracionHoras + " horas: Inicio " + horaInicio + ", Fin: " + horaFin + " al grupo " + grupo.getIdGrupo());
        return new Gen(grupo, profesor, aula, dia, horaInicio, horaFin); // Crear y devolver el gen
    }

    // Método auxiliar que permite crear un bloque de 2 horas por defecto
    private Gen crearGenParaBloque(Grupo grupo) {
        // Obtener el profesor asignado a la asignatura del grupo
        Profesor profesor = grupo.getAsignatura().getDictados().stream().findFirst().orElseThrow().getProfesor();
        // Obtener un aula aleatoria que cumpla con los requisitos del grupo
        Aula aula = obtenerAulaAleatoria(grupo.getAsignatura().getNecesitaComputadores());
        // Crear y devolver el gen
        return crearGenParaBloque(grupo, profesor, aula, 16);
    }

    // Método que calcula la aptitud de un cromosoma según las restricciones definidas
    private void calcularAptitud(Cromosoma cromosoma) {
        // Calcular la aptitud utilizando las restricciones definidas en la clase RestriccionesAlgoritmoGenetico
        int aptitud = restriccionesAlgoritmoGenetico.calcularAptitud(cromosoma);
        // Asignar la aptitud calculada al cromosoma
        cromosoma.setAptitud(aptitud);
    }

    // Método que selecciona una subpoblación de cromosomas basada en su aptitud
    private List<Cromosoma> seleccionar(List<Cromosoma> poblacion) {
        Random random = new Random();
        // Seleccionar cromosomas mediante un proceso de torneo
        return IntStream.range(0, poblacion.size())
                .mapToObj(i -> IntStream.range(0, 5)
                        .mapToObj(j -> poblacion.get(random.nextInt(poblacion.size())))
                        .max(Comparator.comparing(Cromosoma::getAptitud))
                        .orElseThrow())
                .collect(Collectors.toList()); // Devolver la lista de cromosomas seleccionados
    }

    // Método que aplica la operación de cruce entre pares de cromosomas
    private List<Cromosoma> cruzar(List<Cromosoma> seleccionados) {
        Random random = new Random();
        // Realizar el cruce entre pares de cromosomas para generar nueva descendencia
        return IntStream.range(0, seleccionados.size() / 2)
                .mapToObj(i -> {
                    Cromosoma padre1 = seleccionados.get(random.nextInt(seleccionados.size()));
                    Cromosoma padre2 = seleccionados.get(random.nextInt(seleccionados.size()));
                    int puntoCruce = random.nextInt(padre1.getGenes().size());

                    // Crear dos nuevos cromosomas combinando los genes de los padres
                    List<Gen> genesHijo1 = new ArrayList<>(padre1.getGenes().subList(0, puntoCruce));
                    genesHijo1.addAll(padre2.getGenes().subList(puntoCruce, padre2.getGenes().size()));

                    List<Gen> genesHijo2 = new ArrayList<>(padre2.getGenes().subList(0, puntoCruce));
                    genesHijo2.addAll(padre1.getGenes().subList(puntoCruce, padre1.getGenes().size()));

                    return Arrays.asList(new Cromosoma(genesHijo1), new Cromosoma(genesHijo2));
                })
                .flatMap(List::stream) // Combinar los cromosomas generados en un solo stream
                .collect(Collectors.toList()); // Devolver la nueva población
    }

    // Método que aplica mutaciones a los genes de un cromosoma con una probabilidad del 10%
    private void mutar(Cromosoma cromosoma) {
        Random random = new Random();
        cromosoma.getGenes().forEach(gen -> {
            if (random.nextDouble() < 0.1) { // Probabilidad de mutación del 10%
                // Cambiar aleatoriamente el día del gen
                gen.setDia(obtenerDiaAleatorio(cromosoma.getGenes(), gen.getGrupo()));
                // Cambiar aleatoriamente la hora de inicio del gen
                gen.setHoraInicio(obtenerHoraInicioAleatoria(16));
                // Ajustar la hora de fin del gen en consecuencia
                gen.setHoraFin(new Time(gen.getHoraInicio().getTime() + 2 * 60 * 60 * 1000)); // 2 horas adicionales
                // Cambiar aleatoriamente el aula asignada al gen
                gen.setAula(obtenerAulaAleatoria(gen.getGrupo().getAsignatura().getNecesitaComputadores()));
            }
        });
    }

    // Método que encuentra el cromosoma con la mayor aptitud en la población
    private Cromosoma encontrarMejorSolucion(List<Cromosoma> poblacion) {
        // Retornar el cromosoma con la máxima aptitud
        return poblacion.stream()
                .max(Comparator.comparing(Cromosoma::getAptitud))
                .orElse(null);
    }

    // Método que guarda la mejor solución encontrada en la base de datos, aplicando reparaciones si es necesario
    private void guardarMejorSolucion(Cromosoma mejorSolucion) {
        StringBuilder errores = new StringBuilder();

        // Verificar si hay solapamientos de aulas y aplicar correcciones si es necesario
        if (restriccionesAlgoritmoGenetico.haySolapamientoAula(mejorSolucion)) {
            errores.append("Solapamiento de aulas detectado. Intentando corregir...\n");
            reparacionAlgoritmoGenetico.corregirSolapamientoAula(mejorSolucion.getGenes());
        }

        // Verificar si hay solapamientos de profesores y aplicar correcciones si es necesario
        if (restriccionesAlgoritmoGenetico.haySolapamientoProfesor(mejorSolucion)) {
            errores.append("Solapamiento de profesores detectado. Intentando corregir...\n");
            reparacionAlgoritmoGenetico.corregirSolapamientoProfesor(mejorSolucion.getGenes());
        }

        // Verificar nuevamente después de la corrección si los solapamientos han sido resueltos
        boolean solapamientoAulaPostCorreccion = restriccionesAlgoritmoGenetico.haySolapamientoAula(mejorSolucion);
        boolean solapamientoProfesorPostCorreccion = restriccionesAlgoritmoGenetico.haySolapamientoProfesor(mejorSolucion);

        if (!solapamientoAulaPostCorreccion && !solapamientoProfesorPostCorreccion) {
            // Si no hay solapamientos, proceder a guardar los horarios en la base de datos
            mejorSolucion.getGenes().forEach(gen -> {
                Horario horario = crearHorarioDesdeGen(gen);
                horarioRepository.save(horario);
            });
            logs.add("Horarios guardados exitosamente después de las correcciones.");
        } else {
            // Si los errores persisten después de las correcciones, registrar los errores en los logs
            if (solapamientoAulaPostCorreccion) {
                errores.append("Error: Solapamiento de aulas persistente después de la corrección.\n");
            }
            if (solapamientoProfesorPostCorreccion) {
                errores.append("Error: Solapamiento de profesores persistente después de la corrección.\n");
            }
            logs.add(errores.toString());
        }
    }

    // Método que crea un objeto Horario a partir de un Gen
    private Horario crearHorarioDesdeGen(Gen gen) {
        // Crear un nuevo objeto Horario y asignar los valores correspondientes del gen
        Horario horario = new Horario();
        horario.setGrupo(gen.getGrupo());
        horario.setProfesor(gen.getProfesor());
        horario.setAula(gen.getAula());
        horario.setDia(gen.getDia());
        horario.setHoraInicio(gen.getHoraInicio());
        horario.setHoraFin(gen.getHoraFin());
        return horario; // Devolver el objeto Horario creado
    }

    // Métodos auxiliares para obtener datos aleatorios

    // Método para obtener un aula aleatoria que cumpla con la necesidad de computadores
    private Aula obtenerAulaAleatoria(boolean necesitaComputadores) {
        return aulaService.obtenerAulaAleatoria(necesitaComputadores);
    }

    // Método para generar una hora de inicio aleatoria dentro del rango permitido
    private Time obtenerHoraInicioAleatoria(int maxHorasDisponibles) {
        int hora = 6 + new Random().nextInt(maxHorasDisponibles);
        return Time.valueOf(String.format("%02d:00:00", hora)); // Devolver la hora de inicio en formato Time
    }

    // Método para seleccionar un día aleatorio, asegurándose de que no haya conflicto con otros genes del mismo grupo
    private String obtenerDiaAleatorio(List<Gen> genes, Grupo grupoActual) {
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"};
        // Filtrar los días que no tienen conflictos con los genes existentes
        List<String> diasDisponibles = Arrays.stream(dias)
                .filter(dia -> genes.stream().noneMatch(gen -> gen.getGrupo().equals(grupoActual) && gen.getDia().equals(dia)))
                .toList();

        // Devolver un día aleatorio disponible o cualquier día si todos los días están en conflicto
        return diasDisponibles.isEmpty() ? dias[new Random().nextInt(dias.length)] : diasDisponibles.get(new Random().nextInt(diasDisponibles.size()));
    }

    // Método para mostrar los logs generados durante la ejecución del algoritmo
    public void mostrarLogs() {
        logs.forEach(System.out::println);
    }
}
