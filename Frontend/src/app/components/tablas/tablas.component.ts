import { Component, Input, Output, EventEmitter, OnInit} from '@angular/core';
import { Acciones } from '../../interfaces/acciones';
import { Horarios } from '../../interfaces/horarios';
import { isEmpty } from 'rxjs';

@Component({
  selector: 'app-tablas',
  standalone: true,
  imports: [],
  templateUrl: './tablas.component.html',
  styleUrl: './tablas.component.css'
})
export class TablasComponent implements OnInit{

  title:string = '';
  columns:string[] = [];
  dataSource:any = [];
  dataEmpty:boolean = false;
  

  @Input() set titulo(title: any){
    this.title = title;
  }

  @Input() set columnas(columns: string[]){
    this.columns = columns;
  }

  @Input() set data(data: any) {
    this.dataSource = data;
    this.dataEmpty = Object.keys(this.dataSource).length === 0;
    this.currentPage = 1; 
    this.updatePaginatedData(); 
  }

  @Output() action: EventEmitter<Acciones> = new EventEmitter<Acciones>();

  onAction(accion: string, row?: any) {
    this.action.emit({accion: accion, fila: row})
  }

  getNestedProperty(item: string, data:any): any {
    if (item === 'nombreDocente') {
      return `${data.nombre1} ${data.nombre2} ${data.apellido1} ${data.apellido2}`.trim();
    }else{
      if (typeof data[item] === 'object'){
        if (item === 'usuario'){
          return data[item]['nombreUsuario']
        }else if (item === 'rol') {
          return data[item]['nombreRol']
        }else{
          return data[item]['nombre']
        }}else{
        return data[item]
      }
    }

  }

  columnMap: { [key: string]: string } = {
    idEdificio: 'Id',
    idAsignatura: 'Id',
    idGrupo: 'Id',
    idAula: 'Id',
    idProfesor: 'Id',
    idUsuario: 'Id',

    horasTeoria: 'Horas Teoricas',
    horasPractica: 'Horas Practicas',
    nombreGrupo: 'Nombre del Grupo',
    documentoIdentidad: 'Documento de Identidad',
    nombreDocente: 'Nombre del docente',
    nombreUsuario: 'Usuario',
    contrasena: 'Contraseña',
  };

  getColumnName(column: string): string {
    return this.columnMap[column] || column;
  }



  itemsPerPage: number = 13; // Número de elementos por página
  currentPage: number = 1; // Página actual
  paginatedData: any[] = []; // Datos paginados

  ngOnInit(): void {
    this.updatePaginatedData();
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

}
