import { Component } from '@angular/core';
import { TablasComponent } from '../../../../components/tablas/tablas.component';
import { ApiService } from '../../../../services/api.service';
import { Aula } from '../../../../interfaces/horarios';
import { Acciones, getEntityPropiedades } from '../../../../interfaces/acciones';
import { NewComponent } from '../../../../components/new/new.component';
import { Router, RouterOutlet, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-aulas',
  standalone: true,
  imports: [
    TablasComponent, 
    NewComponent, 
    RouterOutlet,
  ],
  templateUrl: './aulas.component.html',
  styleUrl: './aulas.component.css'
})
export class AulasComponent {
  constructor(private router:Router, private Aroute:ActivatedRoute ,private service:ApiService) {}

  private url:string = '/aulas'

  dataAulas: Aula[] = [];
  columnas: string[] = [];
  title: string = 'Aulas'

  ngOnInit(): void {
    this.columnas = getEntityPropiedades('aulas');  
  
    // this.service.getData(this.url).subscribe(data => {
    //   this.dataAulas = data;
    // })
  }

  onAction(accion: Acciones) {
    if (accion.accion == 'Editar') {
      this.editar(accion.fila)
    } else if (accion.accion == 'Borrar') {
      this.eliminar(accion.fila.idAula)
    }
  }

  editar(objeto:any) {
    this.router.navigate(['modificar'], {
      relativeTo: this.Aroute,
      state: { columns: this.columnas, data: objeto, url: this.url}
    });
  }

  eliminar(idObjeto:number) {
    this.service.deleteDataId(this.url, idObjeto)
  }
}