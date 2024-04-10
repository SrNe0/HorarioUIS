package uis.horariouis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.horariouis.model.HorarioProfesor;
import uis.horariouis.model.Profesor;

import java.util.List;

@Repository
public interface HorarioProfesorRepository extends JpaRepository<HorarioProfesor, Long> {
    // Aquí puedes agregar métodos de consulta adicionales si los necesitas
    List<HorarioProfesor> findByProfesor(Profesor profesor);
}
