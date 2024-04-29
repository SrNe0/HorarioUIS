package uis.horariouis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.horariouis.model.HorarioProfesor;

@Repository
public interface HorarioProfesorRepository extends JpaRepository<HorarioProfesor, Long> {
}
