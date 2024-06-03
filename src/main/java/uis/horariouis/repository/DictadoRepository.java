package uis.horariouis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.horariouis.model.Asignatura;
import uis.horariouis.model.Dictado;

import java.util.List;

@Repository
public interface DictadoRepository extends JpaRepository<Dictado, Long> {
    List<Dictado> findByAsignatura(Asignatura asignatura);
    // Aquí puedes agregar métodos de consulta adicionales si los necesitas
}
