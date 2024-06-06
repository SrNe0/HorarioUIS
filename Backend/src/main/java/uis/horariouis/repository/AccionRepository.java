package uis.horariouis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.horariouis.model.Accion;

@Repository
public interface AccionRepository extends JpaRepository<Accion, Long> {
    // Aquí puedes agregar métodos de consulta adicionales si los necesitas
}