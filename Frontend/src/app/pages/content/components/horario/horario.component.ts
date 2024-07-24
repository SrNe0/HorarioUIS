import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../../../services/api.service';
import { TablasComponent } from '../../../../components/tablas/tablas.component';
import { Horarios } from '../../../../interfaces/horarios';
import { getEntityPropiedades } from '../../../../interfaces/acciones';

@Component({
  selector: 'app-horario',
  standalone: true,
  imports: [TablasComponent],
  templateUrl: './horario.component.html',
  styleUrl: './horario.component.css'
})
export class HorarioComponent implements OnInit{
  constructor(private service:ApiService){}

  private url:string = '/horarios';


  dataHorario: Horarios[] =[];
  columnas: string[] = [];

  ngOnInit(): void {
    this.columnas = getEntityPropiedades('horario');  
  
    this.service.getData(this.url).subscribe(data => {
      this.dataHorario = data;
      
      console.log(this.dataHorario)
    })
  
  }

}
