import { Component, OnInit, ViewContainerRef } from '@angular/core';
import { TablasComponent } from "../../../../components/tablas/tablas.component";
import { DelayService } from '../../../../services/delay.service';
import { ApiService } from '../../../../services/api.service';
import { Edificio } from '../../../../interfaces/horarios';
import { Acciones, getEntityPropiedades } from '../../../../interfaces/acciones';
import { Router, RouterOutlet, ActivatedRoute } from '@angular/router';
import { NewComponent } from '../../../../components/new/new.component';
import { ConfirmacionComponent } from '../../../../components/confirmacion/confirmacion.component';

@Component({
  selector: 'app-edificios',
  standalone: true,
  imports: [
    TablasComponent, 
    NewComponent, 
    RouterOutlet, 
    ConfirmacionComponent
  ],
  templateUrl: './edificios.component.html',
  styleUrl: './edificios.component.css'
})
export class EdificiosComponent implements OnInit{
  constructor(
    private router:Router,
    private Aroute:ActivatedRoute,
    private service:ApiService,
    private delayService: DelayService,
    private VCR: ViewContainerRef
  ) {}

  private url:string = '/edificios'

  mensaje: string = '¿Estás seguro de que desea eliminar esta asignatura?';
  showConfirmDialog: boolean = false;
  objetoAEliminar?: Edificio;
  nombreObjeto: string = '';

  dataEdificios: Edificio[] = [];
  columnas: string[] = [];
  title: string = 'Edificios'

  ngOnInit(): void {
    this.columnas = getEntityPropiedades('edificios');  

    this.delayService.setViewContainerRef(this.VCR);
  
    this.delayService.applyDelayWithLoading(600).subscribe(() =>{
      this.service.data$.subscribe(data => {
        this.dataEdificios = data;
      });
      this.service.getData(this.url).subscribe(); 
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
    this.delayService.applyDelayWithLoading(1000).subscribe(() => {
      this.router.navigate(['nuevo'], {
        relativeTo: this.Aroute,
        state: { columns: this.columnas, url: this.url }
      }).then(() => {
        this.loadData();
      });
    });
  }

  editar(objeto: any) {
    this.delayService.applyDelayWithLoading(1000).subscribe(() => {
      this.router.navigate(['modificar'], {
        relativeTo: this.Aroute,
        state: { columns: this.columnas, data: objeto, url: this.url }
      }).then(() => {
        this.loadData();
      });
    });
  } 

  confirmarEliminacion(confirmado: boolean) {
    this.delayService.applyDelayWithLoading(500).subscribe(() => {
      if (confirmado && this.objetoAEliminar) {
        this.service.deleteDataId(this.url, this.objetoAEliminar.idEdificio).subscribe({
      });
      }
      this.showConfirmDialog = false;
      this.objetoAEliminar = null!;
    });
  }

  loadData(): void {
    this.delayService.applyDelayWithLoading(500).subscribe(() => {
      this.service.getData(this.url).subscribe(data => {
        this.dataEdificios = data;
      });
    });
  }
}
