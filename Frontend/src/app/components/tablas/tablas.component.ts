import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges} from '@angular/core';
import { Acciones } from '../../interfaces/acciones';
import { RouterLink, RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-tablas',
  standalone: true,
  imports: [
    RouterLink,
    RouterOutlet
  ],
  templateUrl: './tablas.component.html',
  styleUrl: './tablas.component.css'
})
export class TablasComponent implements OnInit, OnChanges{

  title:string = '';
  columns:string[] = [];
  dataSource:any[] = [];
  dataEmpty:boolean = false;
  

  @Input() set titulo(title: any){
    this.title = title;
  }

  @Input() set columnas(columns: string[]){
    this.columns = columns;
  }

  @Input() set data(data: any) {
    this.dataSource = Array.isArray(data) ? data : [];
    this.dataEmpty = this.dataSource.length === 0;
    this.currentPage = 1; 
    this.updatePaginatedData(); 
  }

  @Output() action: EventEmitter<Acciones> = new EventEmitter<Acciones>();

  onAction(accion: string, objeto?: any) {
    this.action.emit({accion: accion, fila: objeto})
  }

  ngOnInit(): void {
    this.updatePaginatedData();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['data']) {
      this.updatePaginatedData();
    }
  }

  updatePaginatedData() {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.paginatedData = this.dataSource.slice(startIndex, endIndex);
  }

  goToPage(page: number) {
    this.currentPage = page;
    this.updatePaginatedData();
  }

  get totalPages(): number {
    return Math.ceil(this.dataSource.length / this.itemsPerPage);
  }

  getColumnName(column: string): string {
    return this.columnMap[column] || column;
  }

  columnNameIgnore(item:string){
    if (item === 'Id' || item === 'nombre1' || item === 'nombre2' || item === 'apellido1' || item === 'apellido2' || item === 'rol'){
      return false
    }else {
      return true
    }
  }

  itemsPerPage: number = 10; 
  currentPage: number = 1; 
  paginatedData: any[] = []; 

  getNestedProperty(item: string, data:any): any {
    if (item === 'nombreDocente' && this.title === 'Horarios') {
      return `${data.profesor.nombre1} ${data.profesor.nombre2} ${data.profesor.apellido1} ${data.profesor.apellido2}`.trim();
    }else if (item === 'nombreDocente'){
      return `${data.nombre1} ${data.nombre2} ${data.apellido1} ${data.apellido2}`.trim();
    }else if (item === 'asignatura' && this.title === 'Horarios'){
      return data.grupo[item].nombre;
    }else if(item === 'tieneComputadores' || item == 'necesitaComputadores'){
      if (data[item] === true){
        return 'Si';
      }else{
        return 'No';
      }
    }else if (typeof data[item] === 'object'){
      if (item === 'usuario'){
          return data[item]['nombreUsuario'];
      }else if (item === 'rol') {
          return data[item]['nombreRol'];
      }else if (item === 'grupo') {
          return data[item]['nombreGrupo'];
      }else if (item === 'aula') {
          return data[item]['codigo'];
      }else if (item === 'asignatura') {
        return data[item]['nombre'];
      }else{
          return data[item]['nombre'];
      }
      }else{
        return data[item];
    }

  }

  columnMap: { [key: string]: string } = {
    idEdificio: 'Id',
    idAsignatura: 'Id',
    idGrupo: 'Id',
    idAula: 'Id',
    idProfesor: 'Id',
    idUsuario: 'Id',
    idHorario: 'Id',

    horasTeoria: 'Horas Teoricas',
    horasPractica: 'Horas Practicas',
    nombreGrupo: 'Nombre del Grupo',
    documentoIdentidad: 'Documento de Identidad',
    nombreDocente: 'Nombre del docente',
    nombreUsuario: 'Usuario',
    contrasena: 'Contraseña',
    horaInicio: 'Hora de Inicio',
    horaFin: 'Hora Final',
    tieneComputadores: 'Tiene Computadores',
    codigoAsignatura: 'asignatura',
  };
}