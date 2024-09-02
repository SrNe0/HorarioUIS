import { Component, OnInit } from '@angular/core';
import { TablasComponent } from '../../../../components/tablas/tablas.component';
import { ApiService } from '../../../../services/api.service';
import { Profesor } from '../../../../interfaces/horarios';
import { Acciones, getEntityPropiedades } from '../../../../interfaces/acciones';
import { NewComponent } from '../../../../components/new/new.component';
import { Router, RouterOutlet, ActivatedRoute } from '@angular/router';
import { ConfirmacionComponent } from '../../../../components/confirmacion/confirmacion.component';

@Component({
  selector: 'app-profesores',
  standalone: true,
  imports: [
    TablasComponent, 
    NewComponent, 
    RouterOutlet,
    ConfirmacionComponent 
  ],
  templateUrl: './profesores.component.html',
  styleUrl: './profesores.component.css'
})
export class ProfesoresComponent implements OnInit{
  constructor(private router: Router, private Aroute: ActivatedRoute, private service: ApiService) {}

  private url:string = '/profesores'

  mensaje: string = '¿Estás seguro de que desea eliminar esta asignatura?';
  showConfirmDialog: boolean = false;
  objetoAEliminar?: Profesor;
  nombreObjeto: string = '';

  dataProfes: Profesor[] = [];
  columnas: string[] = [];
  title: string = 'Profesores'

  ngOnInit(): void {
    this.columnas = getEntityPropiedades('profesores');  
  
    this.service.data$.subscribe(data => {
      this.dataProfes = data;
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
      this.service.deleteDataId(this.url, this.objetoAEliminar.idProfesor).subscribe({
    });
    }
    this.showConfirmDialog = false;
    this.objetoAEliminar = null!;
  }

  loadData(): void {
    this.service.getData(this.url).subscribe(data => {
      this.dataProfes = data;
    });
  }
}
