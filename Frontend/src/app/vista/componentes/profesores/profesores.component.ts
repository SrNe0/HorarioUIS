import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../../services/api/api.service';
ApiService

@Component({
  selector: 'app-profesores',
  templateUrl: './profesores.component.html',
  styleUrl: './profesores.component.css'
})
export class ProfesoresComponent implements OnInit{
  data:any[] = [];

  constructor(private dataAS:ApiService){}

  private getDataProf(url:string){
    this.dataAS.getData(url).subscribe(data =>{this.data = data})
  }

  ngOnInit(): void {
    this.getDataProf('profesores')
  }
}
