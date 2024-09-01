import { Component, OnInit} from '@angular/core';
import { TablasComponent } from "../../../../components/tablas/tablas.component";
import { ApiService } from '../../../../services/api.service';
import { Asignatura } from '../../../../interfaces/horarios';
import { Acciones, getEntityPropiedades } from '../../../../interfaces/acciones';
import { NewComponent } from '../../../../components/new/new.component';
import { Router, RouterOutlet, ActivatedRoute } from '@angular/router';
import { ConfirmacionComponent } from '../../../../components/confirmacion/confirmacion.component';
import { ModifyComponent } from '../../../../components/modify/modify.component';

@Component({
  selector: 'app-asignaturas',
  standalone: true,
  imports: [
    TablasComponent, 
    NewComponent, 
    RouterOutlet,
    ConfirmacionComponent 
  ],
  templateUrl: './asignaturas.component.html',
  styleUrl: './asignaturas.component.css'
})
export class AsignaturasComponent implements OnInit{
  constructor(private router: Router, private Aroute: ActivatedRoute, private service: ApiService) {}

  private url: string = '/asignaturas';

  mensaje: string = '¿Estás seguro de que desea eliminar esta asignatura?';
  showConfirmDialog: boolean = false;
  objetoAEliminar?: Asignatura;
  nombreObjeto: string = '';
  dataAsignatura: Asignatura[] = [];
  columnas: string[] = [];
  title: string = 'Asignaturas';

  ngOnInit(): void {
    this.columnas = getEntityPropiedades('asignaturas');
    this.service.data$.subscribe(data => {
      this.dataAsignatura = data;
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
      this.service.deleteDataId(this.url, this.objetoAEliminar.idAsignatura).subscribe({
    });
    }
    this.showConfirmDialog = false;
    this.objetoAEliminar = null!;
  }

  loadData(): void {
    this.service.getData(this.url).subscribe(data => {
      this.dataAsignatura = data;
    });
  }
}