package uis.horariouis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.horariouis.model.Asignatura;

@Repository
public interface AsignaturaRepository extends JpaRepository<Asignatura, Long> {
    // Aquí puedes agregar métodos personalizados si necesitas realizar consultas específicas
    Asignatura findByCodigo(String codigo);
}
