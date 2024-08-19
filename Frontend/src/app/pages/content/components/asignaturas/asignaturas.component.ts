import { Component, OnInit } from '@angular/core';
import { TablasComponent } from "../../../../components/tablas/tablas.component";
import { ApiService } from '../../../../services/api.service';
import { Asignatura } from '../../../../interfaces/horarios';
import { Acciones, getEntityPropiedades } from '../../../../interfaces/acciones';
import { NewComponent } from '../../../../components/new/new.component';
import { Router, RouterOutlet, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-asignaturas',
  standalone: true,
  imports: [
    TablasComponent, 
    NewComponent, 
    RouterOutlet, 
  ],
  templateUrl: './asignaturas.component.html',
  styleUrl: './asignaturas.component.css'
})
export class AsignaturasComponent {
  constructor(private router:Router, private Aroute:ActivatedRoute ,private service:ApiService) {}

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
      this.eliminar(accion.fila)
    }
  }

  editar(objeto:any) {
    this.router.navigate(['modificar'], {
      relativeTo: this.Aroute,
      state: { columns: this.columnas, data: objeto, url: this.url}
    });
  }

  eliminar(objeto:any) {
    console.log('eliminando:', objeto.idAsignatura)
    this.service.deleteDataId(this.url, objeto.idAsignatura)
  }
}
