import { Component, OnInit} from '@angular/core';
import { ApiService } from '../../../services/api/api.service';
ApiService

@Component({
  selector: 'app-aulas',
  templateUrl: './aulas.component.html',
  styleUrl: './aulas.component.css'
})
export class AulasComponent implements OnInit{
  data:any[] = []

  constructor(private dataAS:ApiService){}
  
  private getDataAulas(url:string){
    this.dataAS.getData(url).subscribe(data => {this.data= data})
  }

  ngOnInit(): void {
    this.getDataAulas('aulas')
  }

}
