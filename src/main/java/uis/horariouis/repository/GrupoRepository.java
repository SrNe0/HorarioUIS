package uis.horariouis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.horariouis.model.Grupo;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    // Aquí puedes agregar métodos personalizados si necesitas realizar consultas específicas

}

