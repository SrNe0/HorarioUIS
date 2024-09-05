package uis.horariouis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.horariouis.model.Asignatura;
import uis.horariouis.model.Dictado;

import java.util.List;

@Repository
public interface DictadoRepository extends JpaRepository<Dictado, Long> {

    // Consulta usando la entidad completa
    List<Dictado> findByAsignatura(Asignatura asignatura);

    // Consulta usando el campo 'idAsignatura' de la entidad 'Asignatura'
    List<Dictado> findByAsignatura_IdAsignatura(Long idAsignatura);
}

