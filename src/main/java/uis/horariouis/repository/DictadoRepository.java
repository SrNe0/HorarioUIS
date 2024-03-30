package uis.horariouis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.horariouis.model.Dictado;

@Repository
public interface DictadoRepository extends JpaRepository<Dictado, Long> {
    // Aquí puedes agregar métodos de consulta adicionales si los necesitas
}
