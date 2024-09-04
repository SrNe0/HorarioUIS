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

@Service // Anotación que indica que esta clase es un servicio de Spring
public class AlgoritmoGeneticoService {

    @Autowired
    private GrupoService grupoService; // Inyecta el servicio de GrupoService

    @Autowired
    private AulaService aulaService; // Inyecta el servicio de AulaService

    @Autowired
    private HorarioRepository horarioRepository; // Inyecta el repositorio de HorarioRepository

    @Autowired
    private AjusteHorasService ajusteHorasService; // Inyecta el servicio de AjusteHorasService

    @Autowired
    private RestriccionesAlgoritmoGenetico restriccionesAlgoritmoGenetico; // Inyecta el servicio de RestriccionesAlgoritmoGenetico

    @Autowired
    private ReparacionAlgoritmoGenetico reparacionAlgoritmoGenetico; // Inyecta el servicio de ReparacionAlgoritmoGenetico

    private final List<String> logs = new ArrayList<>(); // Lista para almacenar los logs generados durante la ejecución del algoritmo

    // Método principal que ejecuta el algoritmo genético para asignar horarios
    public void ejecutarAlgoritmoGenetico() {
        asignarYGuardarBloquesDe5Horas(); // Llama al método para asignar y guardar bloques de 5 horas
        asignarYGuardarBloquesDe4Horas(); // Llama al método para asignar y guardar bloques de 4 horas
        ajusteHorasService.ajustarHorariosDe5Horas(); // Ajusta los horarios de 5 horas
        mostrarLogs(); // Muestra los logs generados
    }

    // Método que asigna y guarda los bloques de 5 horas
    private void asignarYGuardarBloquesDe5Horas() {
        // Obtiene los grupos que tienen asignaturas con 5 horas de teoría
        List<Grupo> grupos5Horas = grupoService.obtenerGruposConProfesor().stream()
                .filter(grupo -> Optional.ofNullable(grupo.getAsignatura())
                        .map(asignatura -> asignatura.getHorasTeoria() == 5)
                        .orElse(false))
                .collect(Collectors.toList());

        // Inicializa la población de cromosomas para estos grupos
        List<Cromosoma> poblacion5Horas = inicializarPoblacion5Horas(grupos5Horas, 400); // Aumentamos la población a 400 cromosomas

        // Realiza 800 iteraciones del algoritmo genético
        for (int i = 0; i < 800; i++) { // Duplicamos las iteraciones de 400 a 800
            poblacion5Horas.forEach(this::calcularAptitud); // Calcula la aptitud de cada cromosoma
            List<Cromosoma> seleccionados = seleccionar(poblacion5Horas); // Selecciona los cromosomas más aptos
            List<Cromosoma> nuevaPoblacion = cruzar(seleccionados); // Aplica el cruce para generar una nueva población
            nuevaPoblacion.forEach(this::mutar); // Aplica la mutación a la nueva población
            poblacion5Horas = nuevaPoblacion; // Reemplaza la población antigua con la nueva
        }

        // Guarda la mejor solución encontrada en la base de datos
        guardarMejorSolucion(encontrarMejorSolucion(poblacion5Horas));
    }

    // Método que asigna y guarda los bloques de 4 horas
    private void asignarYGuardarBloquesDe4Horas() {
        // Obtiene los grupos que tienen asignaturas con 4 horas de teoría
        List<Grupo> grupos4Horas = grupoService.obtenerGruposConProfesor().stream()
                .filter(grupo -> Optional.ofNullable(grupo.getAsignatura())
                        .map(asignatura -> asignatura.getHorasTeoria() == 4)
                        .orElse(false))
                .collect(Collectors.toList());

        // Inicializa la población de cromosomas para estos grupos
        List<Cromosoma> poblacion4Horas = inicializarPoblacion4Horas(grupos4Horas, 200); // Aumentamos la población a 200 cromosomas

        // Realiza 800 iteraciones del algoritmo genético
        for (int i = 0; i < 800; i++) { // Duplicamos las iteraciones de 400 a 800
            poblacion4Horas.forEach(this::calcularAptitud); // Calcula la aptitud de cada cromosoma
            List<Cromosoma> seleccionados = seleccionar(poblacion4Horas); // Selecciona los cromosomas más aptos
            List<Cromosoma> nuevaPoblacion = cruzar(seleccionados); // Aplica el cruce para generar una nueva población
            nuevaPoblacion.forEach(this::mutar); // Aplica la mutación a la nueva población
            poblacion4Horas = nuevaPoblacion; // Reemplaza la población antigua con la nueva
        }

        // Guarda la mejor solución encontrada en la base de datos
        guardarMejorSolucion(encontrarMejorSolucion(poblacion4Horas));
    }

    // Método para inicializar la población de cromosomas para grupos de 5 horas
    private List<Cromosoma> inicializarPoblacion5Horas(List<Grupo> grupos, int tamanoPoblacion) {
        return IntStream.range(0, tamanoPoblacion) // Genera un rango de cromosomas según el tamaño de la población
                .mapToObj(i -> {
                    // Crea los genes para cada grupo
                    List<Gen> genes = grupos.stream()
                            .flatMap(grupo -> crearGenesParaGrupo(grupo).stream())
                            .collect(Collectors.toList());

                    logs.add("Cromosoma " + i + " generado con " + genes.size() + " genes."); // Agrega un log indicando que se generó un cromosoma
                    return new Cromosoma(genes); // Retorna el cromosoma generado
                })
                .collect(Collectors.toList()); // Colecta los cromosomas en una lista
    }

    // Método para inicializar la población de cromosomas para grupos de 4 horas
    private List<Cromosoma> inicializarPoblacion4Horas(List<Grupo> grupos, int tamanoPoblacion) {
        return IntStream.range(0, tamanoPoblacion) // Genera un rango de cromosomas según el tamaño de la población
                .mapToObj(i -> {
                    // Crea dos genes para cada grupo
                    List<Gen> genes = grupos.stream()
                            .flatMap(grupo -> IntStream.range(0, 2)
                                    .mapToObj(j -> crearGenParaBloque(grupo))
                                    .toList().stream())
                            .collect(Collectors.toList());

                    logs.add("Cromosoma " + i + " generado con " + genes.size() + " genes."); // Agrega un log indicando que se generó un cromosoma
                    return new Cromosoma(genes); // Retorna el cromosoma generado
                })
                .collect(Collectors.toList()); // Colecta los cromosomas en una lista
    }

    // Método para crear los genes de un grupo específico
    private List<Gen> crearGenesParaGrupo(Grupo grupo) {
        List<Gen> genes = new ArrayList<>(); // Inicializa una lista de genes
        Profesor profesor = grupo.getAsignatura().getDictados().stream().findFirst().orElseThrow().getProfesor(); // Obtiene el profesor asignado
        Aula aula = obtenerAulaAleatoria(grupo.getAsignatura().getNecesitaComputadores()); // Obtiene un aula aleatoria

        Gen gen1 = crearGenParaBloque(grupo, profesor, aula, 14); // Crea el primer gen (bloque de 2 horas)
        genes.add(gen1); // Agrega el gen a la lista

        Gen gen2;
        do {
            gen2 = crearGenParaBloque(grupo, profesor, aula, 14); // Crea el segundo gen (otro bloque de 2 horas)
        } while (gen2.getDia().equals(gen1.getDia())); // Asegura que no sea el mismo día que el primero

        genes.add(gen2); // Agrega el gen a la lista

        Gen gen3;
        do {
            gen3 = crearGenParaBloque(grupo, profesor, aula, 21, 1); // Crea el tercer gen (bloque de 1 hora)
        } while (gen3.getDia().equals(gen1.getDia()) || gen3.getDia().equals(gen2.getDia())); // Asegura que no sea el mismo día que los anteriores

        genes.add(gen3); // Agrega el gen a la lista

        return genes; // Retorna la lista de genes creados
    }

    // Método para crear un gen representando un bloque de horas para un grupo en un día y hora específicos
    private Gen crearGenParaBloque(Grupo grupo, Profesor profesor, Aula aula, int maxHoraInicio) {
        String dia = obtenerDiaAleatorio(new ArrayList<>(), grupo); // Obtiene un día aleatorio
        Time horaInicio = obtenerHoraInicioAleatoria(maxHoraInicio); // Obtiene una hora de inicio aleatoria
        Time horaFin = Time.valueOf(String.format("%02d:00:00", horaInicio.toLocalTime().getHour() + 2)); // Calcula la hora de fin (2 horas después)
        logs.add("Asignado bloque de 2 horas: Inicio " + horaInicio + ", Fin: " + horaFin + " al grupo " + grupo.getIdGrupo()); // Agrega un log con la información del bloque asignado
        return new Gen(grupo, profesor, aula, dia, horaInicio, horaFin); // Retorna el gen creado
    }

    // Sobrecarga del método anterior para crear un bloque de horas con duración personalizada
    private Gen crearGenParaBloque(Grupo grupo, Profesor profesor, Aula aula, int maxHoraInicio, int duracionHoras) {
        String dia = obtenerDiaAleatorio(new ArrayList<>(), grupo); // Obtiene un día aleatorio
        Time horaInicio = obtenerHoraInicioAleatoria(maxHoraInicio); // Obtiene una hora de inicio aleatoria
        Time horaFin = Time.valueOf(String.format("%02d:00:00", horaInicio.toLocalTime().getHour() + duracionHoras)); // Calcula la hora de fin
        logs.add("Asignado bloque de " + duracionHoras + " horas: Inicio " + horaInicio + ", Fin: " + horaFin + " al grupo " + grupo.getIdGrupo()); // Agrega un log con la información del bloque asignado
        return new Gen(grupo, profesor, aula, dia, horaInicio, horaFin); // Retorna el gen creado
    }

    // Otra sobrecarga del método anterior para crear un bloque sin especificar la duración (default: 2 horas)
    private Gen crearGenParaBloque(Grupo grupo) {
        Profesor profesor = grupo.getAsignatura().getDictados().stream().findFirst().orElseThrow().getProfesor(); // Obtiene el profesor asignado
        Aula aula = obtenerAulaAleatoria(grupo.getAsignatura().getNecesitaComputadores()); // Obtiene un aula aleatoria
        return crearGenParaBloque(grupo, profesor, aula, 16); // Retorna un gen creado
    }

    // Método que calcula la aptitud de un cromosoma
    private void calcularAptitud(Cromosoma cromosoma) {
        int aptitud = restriccionesAlgoritmoGenetico.calcularAptitud(cromosoma); // Calcula la aptitud del cromosoma usando restricciones
        cromosoma.setAptitud(aptitud); // Asigna la aptitud calculada al cromosoma
    }

    // Método que selecciona los cromosomas más aptos de una población
    private List<Cromosoma> seleccionar(List<Cromosoma> poblacion) {
        Random random = new Random(); // Generador de números aleatorios
        return IntStream.range(0, poblacion.size()) // Genera un rango del tamaño de la población
                .mapToObj(i -> IntStream.range(0, 5)
                        .mapToObj(j -> poblacion.get(random.nextInt(poblacion.size()))) // Selecciona un cromosoma aleatorio
                        .max(Comparator.comparing(Cromosoma::getAptitud)) // Escoge el más apto entre 5 seleccionados aleatoriamente
                        .orElseThrow())
                .collect(Collectors.toList()); // Retorna la lista de cromosomas seleccionados
    }

    // Método que aplica el cruce entre cromosomas seleccionados
    private List<Cromosoma> cruzar(List<Cromosoma> seleccionados) {
        Random random = new Random(); // Generador de números aleatorios
        return IntStream.range(0, seleccionados.size() / 2) // Itera por la mitad del tamaño de los seleccionados
                .mapToObj(i -> {
                    Cromosoma padre1 = seleccionados.get(random.nextInt(seleccionados.size())); // Selecciona un padre aleatoriamente
                    Cromosoma padre2 = seleccionados.get(random.nextInt(seleccionados.size())); // Selecciona otro padre aleatoriamente
                    int puntoCruce = random.nextInt(padre1.getGenes().size()); // Define un punto de cruce aleatorio

                    // Crea genes del hijo 1 usando la combinación de los genes de los padres
                    List<Gen> genesHijo1 = new ArrayList<>(padre1.getGenes().subList(0, puntoCruce));
                    genesHijo1.addAll(padre2.getGenes().subList(puntoCruce, padre2.getGenes().size()));

                    // Crea genes del hijo 2 usando la combinación de los genes de los padres
                    List<Gen> genesHijo2 = new ArrayList<>(padre2.getGenes().subList(0, puntoCruce));
                    genesHijo2.addAll(padre1.getGenes().subList(puntoCruce, padre1.getGenes().size()));

                    return Arrays.asList(new Cromosoma(genesHijo1), new Cromosoma(genesHijo2)); // Retorna la lista de hijos generados
                })
                .flatMap(List::stream) // Combina las listas de hijos en una sola lista
                .collect(Collectors.toList()); // Retorna la lista final de hijos
    }

    // Método que aplica mutación a los genes de un cromosoma
    private void mutar(Cromosoma cromosoma) {
        Random random = new Random(); // Generador de números aleatorios
        cromosoma.getGenes().forEach(gen -> {
            if (random.nextDouble() < 0.1) { // Probabilidad de mutación del 10%
                gen.setDia(obtenerDiaAleatorio(cromosoma.getGenes(), gen.getGrupo())); // Asigna un nuevo día aleatorio
                gen.setHoraInicio(obtenerHoraInicioAleatoria(16)); // Asigna una nueva hora de inicio aleatoria
                gen.setHoraFin(new Time(gen.getHoraInicio().getTime() + 2 * 60 * 60 * 1000)); // Asigna una nueva hora de fin (2 horas después)
                gen.setAula(obtenerAulaAleatoria(gen.getGrupo().getAsignatura().getNecesitaComputadores())); // Asigna un nuevo aula aleatoria
            }
        });
    }

    // Método que encuentra el cromosoma con la mayor aptitud en una población
    private Cromosoma encontrarMejorSolucion(List<Cromosoma> poblacion) {
        return poblacion.stream()
                .max(Comparator.comparing(Cromosoma::getAptitud)) // Busca el cromosoma con la mayor aptitud
                .orElse(null); // Retorna la mejor solución encontrada o null si no hay
    }

    // Método que guarda la mejor solución en la base de datos
    private void guardarMejorSolucion(Cromosoma mejorSolucion) {
        StringBuilder errores = new StringBuilder(); // Builder para acumular posibles errores

        // Verificar restricciones y aplicar reparaciones si es necesario
        if (restriccionesAlgoritmoGenetico.haySolapamientoAula(mejorSolucion)) { // Verifica si hay solapamiento de aulas
            errores.append("Solapamiento de aulas detectado. Intentando corregir...\n"); // Agrega un mensaje de error
            reparacionAlgoritmoGenetico.corregirSolapamientoAula(mejorSolucion.getGenes()); // Intenta corregir solapamiento de aulas
        }

        if (restriccionesAlgoritmoGenetico.haySolapamientoProfesor(mejorSolucion)) { // Verifica si hay solapamiento de profesores
            errores.append("Solapamiento de profesores detectado. Intentando corregir...\n"); // Agrega un mensaje de error
            reparacionAlgoritmoGenetico.corregirSolapamientoProfesor(mejorSolucion.getGenes()); // Intenta corregir solapamiento de profesores
        }

        // Verificar nuevamente después de la corrección
        boolean solapamientoAulaPostCorreccion = restriccionesAlgoritmoGenetico.haySolapamientoAula(mejorSolucion); // Verifica solapamiento de aulas tras la corrección
        boolean solapamientoProfesorPostCorreccion = restriccionesAlgoritmoGenetico.haySolapamientoProfesor(mejorSolucion); // Verifica solapamiento de profesores tras la corrección

        if (!solapamientoAulaPostCorreccion && !solapamientoProfesorPostCorreccion) {
            // No hay solapamientos, proceder a guardar
            mejorSolucion.getGenes().forEach(gen -> {
                Horario horario = crearHorarioDesdeGen(gen); // Crea un objeto Horario desde un gen
                horarioRepository.save(horario); // Guarda el horario en la base de datos
            });
            logs.add("Horarios guardados exitosamente después de las correcciones."); // Agrega un log de éxito
        } else {
            // Si los errores persisten después de las correcciones, registrar en los logs
            if (solapamientoAulaPostCorreccion) {
                errores.append("Error: Solapamiento de aulas persistente después de la corrección.\n"); // Agrega un mensaje de error si persiste el solapamiento de aulas
            }
            if (solapamientoProfesorPostCorreccion) {
                errores.append("Error: Solapamiento de profesores persistente después de la corrección.\n"); // Agrega un mensaje de error si persiste el solapamiento de profesores
            }
            logs.add(errores.toString()); // Agrega los errores a los logs
        }
    }

    // Método que crea un objeto Horario desde un gen
    private Horario crearHorarioDesdeGen(Gen gen) {
        Horario horario = new Horario(); // Crea un nuevo objeto Horario
        horario.setGrupo(gen.getGrupo()); // Asigna el grupo al horario
        horario.setProfesor(gen.getProfesor()); // Asigna el profesor al horario
        horario.setAula(gen.getAula()); // Asigna el aula al horario
        horario.setDia(gen.getDia()); // Asigna el día al horario
        horario.setHoraInicio(gen.getHoraInicio()); // Asigna la hora de inicio al horario
        horario.setHoraFin(gen.getHoraFin()); // Asigna la hora de fin al horario
        return horario; // Retorna el objeto Horario creado
    }

    // Métodos auxiliares para obtener datos aleatorios
    private Aula obtenerAulaAleatoria(boolean necesitaComputadores) {
        return aulaService.obtenerAulaAleatoria(necesitaComputadores); // Obtiene un aula aleatoria según si se necesitan computadores
    }

    private Time obtenerHoraInicioAleatoria(int maxHorasDisponibles) {
        int hora = 6 + new Random().nextInt(maxHorasDisponibles); // Genera una hora de inicio aleatoria entre las 6 AM y un máximo dado
        return Time.valueOf(String.format("%02d:00:00", hora)); // Retorna la hora en formato Time
    }

    private String obtenerDiaAleatorio(List<Gen> genes, Grupo grupoActual) {
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"}; // Array con los días de la semana
        List<String> diasDisponibles = Arrays.stream(dias)
                .filter(dia -> genes.stream().noneMatch(gen -> gen.getGrupo().equals(grupoActual) && gen.getDia().equals(dia))) // Filtra los días en los que el grupo no tiene horarios
                .toList();

        return diasDisponibles.isEmpty() ? dias[new Random().nextInt(dias.length)] : diasDisponibles.get(new Random().nextInt(diasDisponibles.size())); // Retorna un día disponible o aleatorio si no hay disponibles
    }

    // Método que muestra los logs generados
    public void mostrarLogs() {
        logs.forEach(System.out::println); // Imprime cada log en la consola
    }
}
