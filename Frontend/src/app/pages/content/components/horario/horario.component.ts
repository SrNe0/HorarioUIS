import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../../../services/api.service';
import { TablasComponent } from '../../../../components/tablas/tablas.component';
import { Horarios } from '../../../../interfaces/horarios';
import { getEntityPropiedades, Acciones } from '../../../../interfaces/acciones';

@Component({
  selector: 'app-horario',
  standalone: true,
  imports: [TablasComponent],
  templateUrl: './horario.component.html',
  styleUrl: './horario.component.css'
})
export class HorarioComponent implements OnInit{
  constructor(private service:ApiService) {}

  private url:string = '/horarios'

  dataHorarios: Horarios[] = [];
  columnas: string[] = [];
  title: string = 'Horarios'

  ngOnInit(): void {
    this.columnas = getEntityPropiedades('horario');  
  
    this.service.getData(this.url).subscribe(data => {
      this.dataHorarios = data;
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
