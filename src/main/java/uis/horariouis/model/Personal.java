package uis.horariouis.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@Table(name = "personal")
public class Personal implements Serializable {

    @Serial
    private static final long serialVersionUID = -123456789L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPersonal;

    @NotBlank
    @Size(min = 1, max = 20)
    @Column(name = "documentoIdentidad", unique = true)
    private String documentoIdentidad;

    @NotBlank
    @Size(min = 1, max = 50)
    @Column(name = "apellidos")
    private String apellidos;

    @NotBlank
    @Size(min = 1, max = 50)
    @Column(name = "nombres")
    private String nombres;

    @NotBlank
    @Size(min = 1, max = 15)
    @Column(name = "telefono")
    private String telefono;

    @NotBlank
    @Size(min = 1, max = 50)
    @Email
    @Column(name = "correo")
    private String correo;

    @NotBlank
    @Size(min = 1, max = 50)
    @Column(name = "condicion")
    private String condicion;

    @NotBlank
    @Size(min = 1, max = 20)
    @Column(name = "nombreUsuario", unique = true)
    private String nombreUsuario;

    @NotBlank
    @Size(min = 6, max = 20)
    @Column(name = "claveAcceso")
    private String claveAcceso;

    @ManyToOne
    @JoinColumn(name = "idRol", referencedColumnName = "idRol")
    private Rol rol;
}

