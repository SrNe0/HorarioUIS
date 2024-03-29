package uis.horariouis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.horariouis.model.Edificio;

@Repository
public interface EdificioRepository extends JpaRepository<Edificio, Long> {
    // Aquí puedes agregar métodos personalizados si necesitas realizar consultas específicas
    Edificio findByNombre(String nombre);
}

