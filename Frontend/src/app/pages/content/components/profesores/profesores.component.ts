import { Component } from '@angular/core';
import { TablasComponent } from '../../../../components/tablas/tablas.component';
import { ApiService } from '../../../../services/api.service';
import { Profesor } from '../../../../interfaces/horarios';
import { Acciones, getEntityPropiedades } from '../../../../interfaces/acciones';

@Component({
  selector: 'app-profesores',
  standalone: true,
  imports: [TablasComponent],
  templateUrl: './profesores.component.html',
  styleUrl: './profesores.component.css'
})
export class ProfesoresComponent {
  constructor(private service:ApiService) {}

  private url:string = '/profesores'

  dataProfes: Profesor[] = [];
  columnas: string[] = [];
  title: string = 'Profesores'

  ngOnInit(): void {
    this.columnas = getEntityPropiedades('profesores');  
  
    this.service.getData(this.url).subscribe(data => {
      this.dataProfes = data;
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
