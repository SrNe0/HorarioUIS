package uis.horariouis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.horariouis.model.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    // Aquí puedes agregar métodos personalizados si necesitas realizar consultas específicas
}

