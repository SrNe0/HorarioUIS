import { Component } from '@angular/core';
import { TablasComponent } from '../../../../components/tablas/tablas.component';
import { ApiService } from '../../../../services/api.service';
import { Aula } from '../../../../interfaces/horarios';
import { Acciones, getEntityPropiedades } from '../../../../interfaces/acciones';

@Component({
  selector: 'app-aulas',
  standalone: true,
  imports: [TablasComponent],
  templateUrl: './aulas.component.html',
  styleUrl: './aulas.component.css'
})
export class AulasComponent {
  constructor(private service:ApiService) {}

  private url:string = '/aulas'

  dataAulas: Aula[] = [];
  columnas: string[] = [];
  title: string = 'Aulas'

  ngOnInit(): void {
    this.columnas = getEntityPropiedades('aulas');  
  
    this.service.getData(this.url).subscribe(data => {
      this.dataAulas = data;
    })
  }

  onAction(accion: Acciones) {
    if (accion.accion == 'Editar') {
      this.editar(accion.fila)
    } else if (accion.accion == 'Borrar') {
      this.eliminar(accion.fila)
    }
  }

  editar(objeto:any) {
    console.log('editar', objeto)
  }

  eliminar(objeto:any) {
    console.log('eliminando:', objeto.idAula)
    this.service.deleteDataId(this.url, objeto.idAula)
  }
}