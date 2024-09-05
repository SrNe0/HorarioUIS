package uis.horariouis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.horariouis.dto.AulaDTO;
import uis.horariouis.model.Aula;
import uis.horariouis.model.Edificio;
import uis.horariouis.model.Horario;
import uis.horariouis.repository.AulaRepository;
import uis.horariouis.repository.EdificioRepository;
import uis.horariouis.repository.HorarioRepository;

import java.sql.Time;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class AulaService {

    @Autowired
    private AulaRepository aulaRepository;

    private final Random random = new Random();

    @Autowired
    private EdificioRepository edificioRepository;
    public List<Aula> getAllAulas() {
        return aulaRepository.findAll();
    }

    public Optional<Aula> getAulaById(Long id) {
        return aulaRepository.findById(id);
    }

    public boolean deleteAula(Long id) {
        if (aulaRepository.existsById(id)) {  // Verifica si la aula existe
            aulaRepository.deleteById(id);  // Elimina la aula
            return true;  // Devuelve true si el aula fue eliminada
        } else {
            return false;  // Devuelve false si la aula no existía
        }
    }

    public Aula createOrUpdateAula(AulaDTO aulaDTO) {
        Edificio edificio = edificioRepository.findByNombre(aulaDTO.getNombreEdificio());
        if (edificio == null) {
            throw new RuntimeException("El edificio con nombre " + aulaDTO.getNombreEdificio() + " no existe.");
        }

        Aula aula = new Aula();
        aula.setCodigo(aulaDTO.getCodigo());
        aula.setDescripcion(aulaDTO.getDescripcion());
        aula.setCapacidad(aulaDTO.getCapacidad());
        aula.setEdificio(edificio); // Asocia el edificio encontrado por nombre
        // Aquí se asigna el valor de tieneComputadores
        aula.setTieneComputadores(aulaDTO.getTieneComputadores());

        return aulaRepository.save(aula);
    }
    public Optional<Aula> updateAula(Long id, AulaDTO aulaDTO) {
        // Verifica si la aula con el ID dado existe
        Optional<Aula> aulaExistente = aulaRepository.findById(id);
        if (aulaExistente.isPresent()) {
            Aula aula = aulaExistente.get();

            // Busca el edificio por nombre
            Edificio edificio = edificioRepository.findByNombre(aulaDTO.getNombreEdificio());
            if (edificio == null) {
                // Manejar el caso en que el edificio no exista, por ejemplo, lanzando una excepción
                throw new RuntimeException("Edificio con nombre " + aulaDTO.getNombreEdificio() + " no encontrado.");
            }

            // Actualiza los campos del aula
            aula.setCodigo(aulaDTO.getCodigo());
            aula.setDescripcion(aulaDTO.getDescripcion());
            aula.setCapacidad(aulaDTO.getCapacidad());
            aula.setEdificio(edificio);

            // Aquí se asigna el valor de tieneComputadores
            aula.setTieneComputadores(aulaDTO.getTieneComputadores());

            // Guarda los cambios en la base de datos
            return Optional.of(aulaRepository.save(aula));
        } else {
            // Retorna un Optional vacío si la aula no existe
            return Optional.empty();
        }
    }



    public Aula obtenerAulaAdecuada(int cupoGrupo, boolean necesitaComputadores) {
        // Filtrar aulas que tengan la capacidad adecuada y, si es necesario, computadoras.
        List<Aula> aulasAdecuadas = aulaRepository.findAll().stream()
                .filter(aula -> aula.getCapacidad() >= cupoGrupo)  // Filtrar aulas con capacidad suficiente
                .filter(aula -> !necesitaComputadores || aula.getTieneComputadores())  // Si necesita computadoras, filtrar aulas que las tengan
                .toList();

        if (aulasAdecuadas.isEmpty()) {
            return null;  // No hay aulas disponibles con suficiente capacidad o computadoras
        }

        return aulasAdecuadas.get(random.nextInt(aulasAdecuadas.size()));  // Seleccionar una aula aleatoriamente entre las adecuadas
    }

    @Autowired
    private HorarioRepository horarioRepository;

    // Método para verificar si hay solapamiento de horarios en un aula
    public boolean existeSolapamiento(Aula aula, int dia, int horaInicio, int horasDuracion) {
        Time horaInicioPropuesta = Time.valueOf(horaInicio + ":00:00");
        Time horaFinPropuesta = Time.valueOf((horaInicio + horasDuracion) + ":00:00");

        // Consulta para verificar solapamientos en el mismo aula y día
        List<Horario> horariosSolapados = horarioRepository.findByAula_IdAulaAndDia(
                aula.getIdAula(), convertirDia(dia)
        );

        for (Horario horario : horariosSolapados) {
            Time horaInicioExistente = horario.getHoraInicio();
            Time horaFinExistente = horario.getHoraFin();

            // Verifica si hay solapamiento de tiempos:
            // (horaInicioPropuesta < horaFinExistente) y (horaFinPropuesta > horaInicioExistente)
            if (horaInicioPropuesta.before(horaFinExistente) && horaFinPropuesta.after(horaInicioExistente)) {
                return true;  // Hay solapamiento
            }
        }

        return false;  // No hay solapamiento
    }





    // Método para convertir el día (número) a un string de día de la semana
    public String convertirDia(int dia) {
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

