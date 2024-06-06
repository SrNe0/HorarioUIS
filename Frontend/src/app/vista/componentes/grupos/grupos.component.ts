import { Component, OnInit} from '@angular/core';
import { ApiService } from '../../../services/api/api.service';
ApiService

@Component({
  selector: 'app-grupos',
  templateUrl: './grupos.component.html',
  styleUrl: './grupos.component.css'
})
export class GruposComponent implements OnInit{
  data:any[] = []

  constructor(private dataAS: ApiService){}

  private getDataGrup(url:string){
    this.dataAS.getData(url).subscribe(data => {this.data = data})
  }
  
  ngOnInit(): void {
    this.getDataGrup('grupos')
  }
}
