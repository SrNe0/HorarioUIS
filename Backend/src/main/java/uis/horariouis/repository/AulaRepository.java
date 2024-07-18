package uis.horariouis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.horariouis.model.Aula;
import uis.horariouis.model.Edificio;

import java.util.Optional;

@Repository
public interface AulaRepository extends JpaRepository<Aula, Long> {
    // Aquí puedes agregar métodos personalizados si necesitas realizar consultas específicas
    Optional<Aula> findByCodigoAndEdificio(String codigo, Edificio edificio);}
