import { Component, OnInit } from '@angular/core';
import { TablasComponent } from '../../../../components/tablas/tablas.component';
import { ApiService } from '../../../../services/api.service';
import { Usuario } from '../../../../interfaces/horarios';
import { Acciones, getEntityPropiedades } from '../../../../interfaces/acciones';
import { NewComponent } from '../../../../components/new/new.component';
import { Router, RouterOutlet, ActivatedRoute } from '@angular/router';
import { ConfirmacionComponent } from '../../../../components/confirmacion/confirmacion.component';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [
    TablasComponent, 
    NewComponent, 
    RouterOutlet,
    ConfirmacionComponent 
  ],
  templateUrl: './usuarios.component.html',
  styleUrl: './usuarios.component.css'
})
export class UsuariosComponent implements OnInit{
  constructor(private router: Router, private Aroute: ActivatedRoute, private service: ApiService) {}

  private url:string = '/usuarios'

  mensaje: string = '¿Estás seguro de que desea eliminar esta asignatura?';
  showConfirmDialog: boolean = false;
  objetoAEliminar?: Usuario;
  nombreObjeto: string = '';

  dataUsuarios: Usuario[] = [];
  columnas: string[] = [];
  title: string = 'Usuarios'

  ngOnInit(): void {
    this.columnas = getEntityPropiedades('usuarios');  
  
    this.service.getData(this.url).subscribe(data => {
      this.dataUsuarios = data;
    }); 
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
      this.service.deleteDataId(this.url, this.objetoAEliminar.idUsuario).subscribe({
    });
    }
    this.showConfirmDialog = false;
    this.objetoAEliminar = null!;
  }

  loadData(): void {
    this.service.getData(this.url).subscribe(data => {
      this.dataUsuarios = data;
    });
  }
}