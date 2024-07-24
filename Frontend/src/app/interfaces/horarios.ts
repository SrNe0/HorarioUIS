export interface Horarios {
    idHorario:  number;
    profesor:   Profesor;
    grupo:      Grupo;
    aula:       Aula;
    dia:        string;
    horaInicio: Date;
    horaFin:    Date;
}

export interface Aula {
    idAula:      number;
    codigo:      string;
    descripcion: string;
    capacidad:   number;
    edificio:    Edificio;
}

export interface Edificio {
    idEdificio: number;
    nombre:     string;
}

export interface Grupo {
    idGrupo:     number;
    asignatura:  Asignatura;
    nombreGrupo: string;
    cupo:        number;
}

export interface Asignatura {
    idAsignatura:  number;
    codigo:        string;
    nombre:        string;
    horasTeoria:   number;
    horasPractica: number;
}

export interface Profesor {
    idProfesor:         number;
    documentoIdentidad: string;
    apellido1:          string;
    apellido2:          string;
    nombre1:            string;
    nombre2:            string;
    telefono:           string;
    correo:             string;
    usuario:            Usuario;
}

export interface Usuario {
    idUsuario:     number;
    nombreUsuario: string;
    contrasena:    string;
    rol:           Rol;
}

export interface Rol {
    idRol:     number;
    nombreRol: string;
}


export class HorariosResponse{
    idHorario =     0;
    nombreDocente = '';
    grupo =         '';
    aula =          '';
    dia =           '';
    horaInicio =    '';
    horaFin =       '';
}

export class AulaResponse {
    idAula =       0;
    codigo =       '';
    descripcion =  '';
    capacidad =    0;
    edificio =     '';
}

export class EdificioResponse {
    idEdificio =  0;
    nombre =      '';
}

export class GrupoResponse {
    idGrupo =      0;
    asignatura =   '';
    nombreGrupo =  '';
    cupo =         0;
}

export class AsignaturaResponse {
    idAsignatura =   0;
    codigo =         '';
    nombre =         '';
    horasTeoria =    0;
    horasPractica =  0;
}

export class ProfesorResponse {
    idProfesor =          0;
    documentoIdentidad =  '';
    nombreDocente =       '';
    telefono =            '';
    correo =              '';
    usuario =             '';
}

export class UsuarioResponse {
    idUsuario =      0;
    nombreUsuario =  '';
    contrasena =     '';
    rol =            '';
}

export class RolResponse {
    idRol =      0;
    nombreRol =  '';
}