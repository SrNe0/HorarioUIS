import { Component } from '@angular/core';
import { TablasComponent } from '../../../../components/tablas/tablas.component';
import { ApiService } from '../../../../services/api.service';
import { Aula } from '../../../../interfaces/horarios';
import { Acciones, getEntityPropiedades } from '../../../../interfaces/acciones';
import { NewComponent } from '../../../../components/new/new.component';
import { Router, RouterOutlet, ActivatedRoute } from '@angular/router';
import { ConfirmacionComponent } from '../../../../components/confirmacion/confirmacion.component';

@Component({
  selector: 'app-aulas',
  standalone: true,
  imports: [
    TablasComponent, 
    NewComponent, 
    RouterOutlet,
    ConfirmacionComponent
  ],
  templateUrl: './aulas.component.html',
  styleUrl: './aulas.component.css'
})
export class AulasComponent {
  constructor(private router:Router, private Aroute:ActivatedRoute ,private service:ApiService) {}

  private url:string = '/aulas'

  mensaje: string = '¿Estás seguro de que desea eliminar esta asignatura?';
  showConfirmDialog: boolean = false;
  objetoAEliminar?: Aula;
  nombreObjeto: string = '';

  dataAulas: Aula[] = [];
  columnas: string[] = [];
  title: string = 'Aulas'

  ngOnInit(): void {
    this.columnas = getEntityPropiedades('aulas');  
  
    this.service.data$.subscribe(data => {
      this.dataAulas = data;
    });
    this.service.getData(this.url).subscribe();
  }
  onAction(accion: Acciones) {
    if (accion.accion == 'Editar') {
      this.editar(accion.fila);
    } else if (accion.accion == 'Borrar') {
      this.objetoAEliminar = accion.fila;
      this.nombreObjeto = accion.fila.nombre;
      this.showConfirmDialog = true;
    } else if (accion.accion == 'Crear') {
      this.crear();
    }
  }

  crear() {
    this.router.navigate(['nuevo'], {
      relativeTo: this.Aroute,
      state: { columns: this.columnas, url: this.url }
    });
  }

  editar(objeto: any) {
    this.router.navigate(['modificar'], {
      relativeTo: this.Aroute,
      state: { columns: this.columnas, data: objeto, url: this.url }
    }).then(() => {
      this.loadData();
    });
  }

  confirmarEliminacion(confirmado: boolean) {
    if (confirmado && this.objetoAEliminar) {
      this.service.deleteDataId(this.url, this.objetoAEliminar.idAula).subscribe({
    });
    }
    this.showConfirmDialog = false;
    this.objetoAEliminar = null!;
  }

  loadData(): void {
    this.service.getData(this.url).subscribe(data => {
      this.dataAulas = data;
    });
  }
}