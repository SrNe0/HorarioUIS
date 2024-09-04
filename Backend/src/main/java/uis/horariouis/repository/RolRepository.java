package uis.horariouis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.horariouis.model.Rol;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Object> findByNombreRol( String nombreRol);
    // Aquí puedes agregar métodos personalizados si necesitas realizar consultas específicas
}

