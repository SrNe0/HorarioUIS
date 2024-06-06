package uis.horariouis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.horariouis.model.Horario;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {
    // Aquí puedes agregar métodos personalizados si necesitas realizar consultas específicas

}
