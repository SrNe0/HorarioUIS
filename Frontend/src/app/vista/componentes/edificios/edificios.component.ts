import { Component, OnInit} from '@angular/core';
import { ApiService } from '../../../services/api/api.service';

@Component({
  selector: 'app-edificios',
  templateUrl: './edificios.component.html',
  styleUrl: './edificios.component.css'
})
export class EdificiosComponent implements OnInit{
  data:any[] = []

  constructor(private dataAS:ApiService){}

  private getDataEdif(url:string){
    this.dataAS.getData(url).subscribe(data => {this.data = data})
  }

  abreviacion(dato:string){
    return dato.slice(0, 1) + dato.charAt(dato.search(/ /) + 1)
  }

  ngOnInit(): void {
    this.getDataEdif('edificios')
  }
}
