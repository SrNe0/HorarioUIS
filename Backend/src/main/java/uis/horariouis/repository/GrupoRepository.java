package uis.horariouis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uis.horariouis.model.Grupo;

import java.util.List;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    // Aquí puedes agregar métodos personalizados si necesitas realizar consultas específicas
    @Query("SELECT g FROM Grupo g WHERE g.asignatura NOT IN (SELECT d.asignatura FROM Dictado d)")
    List<Grupo> findGruposSinProfesorAsignado();

    @Query("SELECT g FROM Grupo g WHERE EXISTS (SELECT d FROM Dictado d WHERE d.asignatura = g.asignatura)")
    List<Grupo> findGruposConProfesorAsignado();

}

