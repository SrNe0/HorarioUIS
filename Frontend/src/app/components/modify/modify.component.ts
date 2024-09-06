import { Component, OnInit, Output, EventEmitter} from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule} from '@angular/forms';
import { Asignatura, Edificio, Usuario } from '../../interfaces/horarios';

@Component({
  selector: 'app-modify',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './modify.component.html',
  styleUrl: './modify.component.css'
})
export class ModifyComponent implements OnInit{
  constructor(private router:Router, private formB: FormBuilder, private service:ApiService){
    this.modifyData = this.formB.group({});
  }

  @Output() dataUpdated = new EventEmitter<any>();

  url?: string;

  modifyData: FormGroup;
  data: any;
  dataEdificios: Edificio[] = [];
  dataAsignaturas: Asignatura[] = [];
  columnas: string[] = [];


  ngOnInit(): void {
    const state = history.state;

    if (state) {
      this.columnas = state.columns;
      this.data = state.data;
      this.url = state.url
    }

    this.loadData()

    console.log(this.data)
    this.columnas.forEach(column => {
      this.modifyData.addControl(column, this.formB.control(this.getNestedProperty(column, this.data), Validators.required));
    });

  }

  loadData(){
    this.service.getData('/asignaturas').subscribe(data => {
      this.dataAsignaturas = data;
    });
    this.service.getData('/edificios').subscribe(data => {
      this.dataEdificios = data;
    });
  }

  getypeof(item:any): any{
    return typeof item
  }

  getColumnName(column: string): string {
    return this.columnMap[column] || column;
  }

  cancelAction(){
    alert('Cancelando accion')
    this.router.navigate(['/usuario' + this.url])
  }

  columnNameIgnore(item:string){
    if (item === 'Id' || item === 'Nombre del docente'){
      return false
    }else {
      return true
    }
  }

  sendAction() {
  const modifyURL = this.url + "/" + this.data[this.columnas[0]];
  console.log(modifyURL)

  //if (this.modifyData.valid) {
    if (true) {
    const updatedData: any = {};

    this.columnas.forEach(column => {
      const originalValue = this.data[column];
      const newValue = this.modifyData.get(column)?.value;

      if (this.columnNameIgnore(this.getColumnName(column))){
        if (typeof originalValue === 'number') {
          updatedData[column] = parseInt(newValue, 10);
        } else if (column === 'edificio'){
          const modifyColumn = 'nombre' + column.charAt(0).toUpperCase() + column.slice(1)
          updatedData[modifyColumn] = newValue
        }else if (column === 'asignatura'){
          const modifyColumn = 'codigo' + column.charAt(0).toUpperCase() + column.slice(1)
          updatedData[modifyColumn] = newValue
        }else if (newValue === 'true'){
          updatedData[column] = true
        }else if (newValue ==='false'){
          updatedData[column] = false
        }else {
          updatedData[column] = newValue;
        }
      }
    });

    console.log(updatedData);
    
    this.service.modifyDataId(modifyURL, updatedData).subscribe({
    next: (response) => {
      this.dataUpdated.emit(this.data);
      alert('Datos guardados con éxito');
      this.router.navigate(['/usuario' + this.url]);
    },
    error: (error) => {
      console.error("Error al modificar los datos:", error);
      alert('Ocurrió un error al guardar los datos.');
    }});

    } else {
      alert('Por favor completa todos los campos requeridos.');
    }
  }

  getNestedProperty(item: string, data: any): any {
    if (typeof data[item] === 'object' && data[item] !== null) {
      return data[item]["nombre"];
    } else if (item === "asignatura"){
      return data[item]['nombre'];
    } else {
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
    idHorario: 'ID',

    horasTeoria: 'Horas Teoricas',
    horasPractica: 'Horas Practicas',
    nombreGrupo: 'Nombre del Grupo',
    documentoIdentidad: 'Documento de Identidad',
    nombreDocente: 'Nombre del docente',
    nombreUsuario: 'Usuario',
    contrasena: 'Contraseña',
    horaInicio: 'Hora de Inicio',
    horaFin: 'Hora Final',
    necesitaComputadores: 'Necesita Computadores',
    tieneComputadores: 'tiene Computadores',
    codigoAsignatura: 'Codigo de la asignatura',
    nombre1: 'Primer Nombre',
    nombre2: 'Segundo Nombre',
    apellido1: 'Primer Apellido',
    apellido2: 'Segundo Apellido',
  };
}

