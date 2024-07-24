import { Component } from '@angular/core';
import { TablasComponent } from '../../../../components/tablas/tablas.component';
import { ApiService } from '../../../../services/api.service';
import { Grupo } from '../../../../interfaces/horarios';
import { Acciones, getEntityPropiedades } from '../../../../interfaces/acciones';

@Component({
  selector: 'app-grupos',
  standalone: true,
  imports: [TablasComponent],
  templateUrl: './grupos.component.html',
  styleUrl: './grupos.component.css'
})
export class GruposComponent {
  constructor(private service:ApiService) {}

  private url:string = '/grupos'

  dataGrupos: Grupo[] = [];
  columnas: string[] = [];
  title: string = 'Grupos'

  ngOnInit(): void {
    this.columnas = getEntityPropiedades('grupos');  
  
    this.service.getData(this.url).subscribe(data => {
      this.dataGrupos = data;
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