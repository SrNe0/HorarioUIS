import { Component, OnInit, ViewContainerRef } from '@angular/core';
import { TablasComponent } from '../../../../components/tablas/tablas.component';
import { DelayService } from '../../../../services/delay.service';
import { ApiService } from '../../../../services/api.service';
import { Grupo } from '../../../../interfaces/horarios';
import { Acciones, getEntityPropiedades } from '../../../../interfaces/acciones';
import { NewComponent } from '../../../../components/new/new.component';
import { Router, RouterOutlet, ActivatedRoute } from '@angular/router';
import { ConfirmacionComponent } from '../../../../components/confirmacion/confirmacion.component';

@Component({
  selector: 'app-grupos',
  standalone: true,
  imports: [
    TablasComponent, 
    NewComponent, 
    RouterOutlet,
    ConfirmacionComponent 
  ],
  templateUrl: './grupos.component.html',
  styleUrl: './grupos.component.css'
})
export class GruposComponent implements OnInit{
  constructor(
    private router: Router, 
    private Aroute: ActivatedRoute, 
    private service: ApiService,
    private delayService: DelayService,
    private VCR: ViewContainerRef
  ) {}

  private url:string = '/grupos'

  mensaje: string = '¿Estás seguro de que desea eliminar esta asignatura?';
  showConfirmDialog: boolean = false;
  objetoAEliminar?: Grupo;
  nombreObjeto: string = '';

  dataGrupos: Grupo[] = [];
  columnas: string[] = [];
  title: string = 'Grupos'

  ngOnInit(): void {
    this.columnas = getEntityPropiedades('grupos');  
  

    this.delayService.setViewContainerRef(this.VCR);
    
    this.delayService.applyDelayWithLoading(600).subscribe(() =>{
      this.service.getData(this.url).subscribe(data => {
        this.dataGrupos = data;
      }) 
    });
  }

  onAction(accion: Acciones) {
    this.delayService.applyDelayWithLoading(500).subscribe(() =>{
      if (accion.accion == 'Editar') {
        this.editar(accion.fila);
      } else if (accion.accion == 'Borrar') {
        this.objetoAEliminar = accion.fila;
        this.nombreObjeto = accion.fila.nombre;
        this.showConfirmDialog = true;
      } else if (accion.accion == 'Crear') {
        this.crear();
      }
    });
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
      this.service.deleteDataId(this.url, this.objetoAEliminar.idGrupo).subscribe({
    });
    }
    this.showConfirmDialog = false;
    this.objetoAEliminar = null!;
  }

  loadData(): void {
    this.service.getData(this.url).subscribe(data => {
      this.dataGrupos = data;
    });
  }
}