import { Component } from '@angular/core';
import { TablasComponent } from '../../../../components/tablas/tablas.component';
import { ApiService } from '../../../../services/api.service';
import { Usuario } from '../../../../interfaces/horarios';
import { Acciones, getEntityPropiedades } from '../../../../interfaces/acciones';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [TablasComponent],
  templateUrl: './usuarios.component.html',
  styleUrl: './usuarios.component.css'
})
export class UsuariosComponent {
  constructor(private service:ApiService) {}

  private url:string = '/usuarios'

  dataUsuarios: Usuario[] = [];
  columnas: string[] = [];
  title: string = 'Usuarios'

  ngOnInit(): void {
    this.columnas = getEntityPropiedades('usuarios');  
  
    this.service.getData(this.url).subscribe(data => {
      this.dataUsuarios = data;
      console.log(this.dataUsuarios)
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