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
    idAula:            number;
    codigo:            string;
    descripcion:       string;
    capacidad:         number;
    tieneComputadores: boolean;
    edificio:          Edificio;
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
    idAsignatura:         number;
    codigo:               string;
    nombre:               string;
    horasTeoria:          number;
    horasPractica:        number;
    necesitaComputadores: boolean;
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
    nombreCompleto:     string;
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
    idHorario =     '';
    grupo =         '';
    asignatura =    '';
    nombreDocente = '';
    aula =          '';
    dia =           '';
    horaInicio =    '';
    horaFin =       '';
}

export class AulaResponse {
    idAula =       '';
    codigo =       '';
    descripcion =  '';
    capacidad =    0;
    edificio =     '';
    tieneComputadores = true;
}

export class EdificioResponse {
    idEdificio =  '';
    nombre =      '';
}

export class GrupoResponse {
    idGrupo =            '';   
    asignatura =   '';
    nombreGrupo =        '';
    cupo =               0;
}

export class AsignaturaResponse {
    idAsignatura =          '';
    codigo =                '';
    nombre =                '';
    horasTeoria =           0;
    horasPractica =         0;
    necesitaComputadores =  true;
}

export class ProfesorResponse {
    idProfesor =          '';
    documentoIdentidad =  '';
    nombreDocente =       '';
    telefono =            '';
    correo =              '';
    usuario =             '';
}

export class UsuarioResponse {
    idUsuario =      '';
    nombreUsuario =  '';
    contrasena =     '';
    rol =            '';
}

export class RolResponse {
    idRol =      '';
    nombreRol =  '';
}

