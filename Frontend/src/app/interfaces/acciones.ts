import { HorariosResponse, AulaResponse, EdificioResponse, GrupoResponse, AsignaturaResponse, ProfesorResponse, UsuarioResponse, RolResponse } from "./horarios";

export interface Acciones<T = any> {
    accion: string;
    fila?: T;
}

export const getEntityPropiedades = (entidad:string): Array<any> => {
    let resultados: any = [];
    let clase: any;

    switch (entidad) {
        case 'horario':
            clase = new HorariosResponse();
            break;
        case 'aulas':
            clase = new AulaResponse();
            break;
        case 'edificios':
            clase = new EdificioResponse();
            break;
        case 'grupos':
            clase = new GrupoResponse();
            break;
        case 'asignaturas':
            clase = new AsignaturaResponse();
            break;
        case 'profesores':
            clase = new ProfesorResponse();
            break;
        case 'usuarios':
            clase = new UsuarioResponse();
            break;
        case 'rol':
            clase = new RolResponse();
            break;
        }

    if (clase) {
        resultados = Object.keys(clase)
    }
    return resultados

}