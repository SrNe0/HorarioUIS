package uis.horariouis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.horariouis.model.Aula;

@Repository
public interface AulaRepository extends JpaRepository<Aula, Long> {
    // Aquí puedes agregar métodos personalizados si necesitas realizar consultas específicas
}
