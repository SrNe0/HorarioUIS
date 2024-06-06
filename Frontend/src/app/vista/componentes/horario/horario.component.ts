import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../../services/api/api.service';


@Component({
  selector: 'app-horario',
  templateUrl: './horario.component.html',
  styleUrl: './horario.component.css'
})
export class HorarioComponent implements OnInit{
  data:any[] = [];

  constructor(private dataAS:ApiService){}

  private getDataHorario(url:string){
    this.dataAS.getData(url).subscribe(data => {this.data = data})
  }

  ngOnInit(): void {
    this.getDataHorario('horarios')
  }
}
