package uis.horariouis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.horariouis.model.RolAccion;

import java.util.List;

@Repository
public interface RolAccionRepository extends JpaRepository<RolAccion, Long> {
    // Aquí puedes agregar métodos de consulta adicionales si los necesitas
    List<RolAccion> findByRolIdRol(Long idRol);
}
