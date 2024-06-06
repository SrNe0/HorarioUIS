package uis.horariouis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.horariouis.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Aquí puedes agregar métodos de consulta adicionales si los necesitas
}
