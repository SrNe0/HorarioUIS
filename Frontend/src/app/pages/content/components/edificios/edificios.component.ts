import { Component, OnInit } from '@angular/core';
import { TablasComponent } from "../../../../components/tablas/tablas.component";
import { ApiService } from '../../../../services/api.service';
import { Edificio } from '../../../../interfaces/horarios';
import { Acciones, getEntityPropiedades } from '../../../../interfaces/acciones';
import { Router, RouterOutlet, ActivatedRoute } from '@angular/router';
import { NewComponent } from '../../../../components/new/new.component';

@Component({
  selector: 'app-edificios',
  standalone: true,
  imports: [
    TablasComponent, 
    NewComponent, 
    RouterOutlet, 
  ],
  templateUrl: './edificios.component.html',
  styleUrl: './edificios.component.css'
})
export class EdificiosComponent implements OnInit{
  constructor(private router:Router, private Aroute:ActivatedRoute, private service:ApiService) {}

  private url:string = '/edificios'

  dataEdificios: Edificio[] = [];
  columnas: string[] = [];
  title: string = 'Edificios'

  ngOnInit(): void {
    this.columnas = getEntityPropiedades('edificios');  
  
    this.service.getData(this.url).subscribe(data => {
      this.dataEdificios = data;
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
    console.log('Objeto a editar: ', objeto)
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
