import { Component, OnInit, Output, EventEmitter} from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule} from '@angular/forms';

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
  dataAnx:any;
  columnas: string[] = [];


  ngOnInit(): void {
    const state = history.state;

    if (state) {
      this.columnas = state.columns;
      this.data = state.data;
      this.url = state.url
    }
    console.log(this.data)
    this.columnas.forEach(column => {
      this.modifyData.addControl(column, this.formB.control(this.getNestedProperty(column, this.data), Validators.required));

      
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

  sendAction() {
  const modifyURL = this.url + "/" + this.data[this.columnas[0]];

  if (this.modifyData.valid) {

    const updatedData: any = {};

    this.columnas.forEach(column => {
      const originalValue = this.data[column];
      const newValue = this.modifyData.get(column)?.value;
      
      if (typeof originalValue === 'number') {
          updatedData[column] = parseInt(newValue, 10);
      } else {
          updatedData[column] = newValue;
      }
    });

    console.log(updatedData);

    } else {
      alert('Por favor completa todos los campos requeridos.');
    }
  }

  getNestedProperty(item: string, data: any): any {
    if (typeof data[item] === 'object' && data[item] !== null) {
      return data[item]["nombre"];
    } else if (item === "codigoAsignatura"){
      return data['asignatura']['codigo'];
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
  };
}

        // this.service.modifyDataId(modifyURL, this.data).subscribe({
      //   next: (response) => {
      //     this.dataUpdated.emit(this.data);
      //     alert('Datos guardados con éxito');
      //     this.router.navigate(['/usuario' + this.url]);
      //   },
      //   error: (error) => {
      //     console.error("Error al modificar los datos:", error);
      //     alert('Ocurrió un error al guardar los datos.');
      //   }
      // });