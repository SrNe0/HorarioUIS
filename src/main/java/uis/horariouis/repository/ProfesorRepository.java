package uis.horariouis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.horariouis.model.Profesor;

@Repository
public interface ProfesorRepository extends JpaRepository<Profesor, Long> {
    Profesor findByDocumentoIdentidad(String documentoIdentidad);
    // Aquí puedes agregar métodos de consulta adicionales si los necesitas
}
