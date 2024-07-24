import { Component, OnInit } from '@angular/core';
import { TablasComponent } from "../../../../components/tablas/tablas.component";
import { ApiService } from '../../../../services/api.service';
import { Asignatura } from '../../../../interfaces/horarios';
import { Acciones, getEntityPropiedades } from '../../../../interfaces/acciones';

@Component({
  selector: 'app-asignaturas',
  standalone: true,
  imports: [TablasComponent],
  templateUrl: './asignaturas.component.html',
  styleUrl: './asignaturas.component.css'
})
export class AsignaturasComponent {
  constructor(private service:ApiService) {}

  private url:string = '/asignaturas'

  dataAsignatura: Asignatura[] = [];
  columnas: string[] = [];
  title: string = 'Asignaturas'

  ngOnInit(): void {
    this.columnas = getEntityPropiedades('asignaturas');  
  
    this.service.getData(this.url).subscribe(data => {
      this.dataAsignatura = data;
    })
  }

  onAction(accion: Acciones) {
    if (accion.accion == 'Editar') {
      this.editar(accion.fila)
    } else if (accion.accion == 'Borrar') {
      this.eliminar(accion.fila.id)
    }
  }

  editar(objeto:any) {
    console.log('editar', objeto)
  }

  eliminar(objeto:any) {
    console.log('editar', objeto)
  }
}
