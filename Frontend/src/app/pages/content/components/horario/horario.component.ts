import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../../../services/api.service';
import { TablasComponent } from '../../../../components/tablas/tablas.component';
import { Horarios } from '../../../../interfaces/horarios';
import { getEntityPropiedades, Acciones } from '../../../../interfaces/acciones';
import { NewComponent } from '../../../../components/new/new.component';
import { Router, RouterOutlet, ActivatedRoute } from '@angular/router';


@Component({
  selector: 'app-horario',
  standalone: true,
  imports: [
    TablasComponent, 
    NewComponent, 
    RouterOutlet,

  ],
  templateUrl: './horario.component.html',
  styleUrl: './horario.component.css'
})
export class HorarioComponent implements OnInit{
  constructor(private router: Router, private Aroute: ActivatedRoute, private service: ApiService) {}

  private url:string = '/horarios'

  dataHorarios: Horarios[] = [];
  columnas: string[] = [];
  title: string = 'Horarios'

  ngOnInit(): void {
    this.columnas = getEntityPropiedades('horario');  
  
    this.service.getData(this.url).subscribe(data => {
      this.dataHorarios = data;
    }); 
  }

  onAction(accion: Acciones) {
    if (accion.accion == 'Editar') {
    } else if (accion.accion == 'Borrar') {

    } else if (accion.accion == 'Crear') {
      this.crear();
    }
  }

  crear() {
  }


  loadData(): void {
    this.service.getData(this.url + '/listar').subscribe(data => {
      this.dataHorarios = data;
    });
  }
}