package uis.horariouis.model;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @Column(nullable = false)
    private String nombreUsuario;

    @Column(nullable = false)
    private String contrasena;

    // Supongamos que cada usuario tiene un rol
    @ManyToOne
    @JoinColumn(name = "idRol")
    private Rol rol;

    // Constructor, getters y setters
}
