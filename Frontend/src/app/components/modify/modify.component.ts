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
  columnas: string[] = [];

  ngOnInit(): void {
    const state = history.state;

    if (state) {
      this.columnas = state.columns;
      this.data = state.data;
      this.url = state.url
    }
    
    this.columnas.forEach(column => {
      this.modifyData.addControl(column, this.formB.control(this.data[column] || '0', Validators.required))
    });

  }

  
  getNestedProperty(item: string, data:any): any {
    if (typeof data[item] === 'object' && data[item] !== null) {
      return data[item]["nombre"];
    } else {
      return data[item];
    }
  }
  columnMap: { [key: string]: string } = {
    idAsignatura: 'Id',
    horasTeoria: 'Horas Teoricas',
    horasPractica: 'Horas Practicas',
  };

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
      this.columnas.forEach(column => {
        if (typeof this.data[column] === 'number') {
          this.data[column] = parseInt(this.modifyData.get(column)?.value, 10);
        } else {
          this.data[column] = this.modifyData.get(column)?.value;
        }
      });
      
      this.service.modifyDataId(modifyURL, this.data).subscribe({
        next: (response) => {
          this.dataUpdated.emit(this.data);
          alert('Datos guardados con éxito');
          this.router.navigate(['/usuario' + this.url]);
        },
        error: (error) => {
          console.error("Error al modificar los datos:", error);
          alert('Ocurrió un error al guardar los datos.');
        }
      });
    } else {
      alert('Por favor completa todos los campos requeridos.');
    }
  }

  dataType(data:string): any{
    return typeof data
  }

  dataAnex(cabecera:string) : void{
    switch(cabecera){
      case ('edificio'):
        console.log('1')
        break;
      default:
        console.log('0')
        break;
    }
  }

}

  