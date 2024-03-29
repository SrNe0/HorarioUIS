package uis.horariouis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.horariouis.model.Personal;

@Repository
public interface PersonalRepository extends JpaRepository<Personal, Long> {
    // Aquí puedes agregar métodos personalizados si necesitas realizar consultas específicas
}

