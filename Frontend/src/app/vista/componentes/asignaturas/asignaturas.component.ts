import { Component, OnInit} from '@angular/core';
import { ApiService } from '../../../services/api/api.service';

@Component({
  selector: 'app-asignaturas',
  templateUrl: './asignaturas.component.html',
  styleUrl: './asignaturas.component.css'
})
export class AsignaturasComponent implements OnInit{
constructor(private dataAS:ApiService){}

data:any[] = []

private getDataAsign(url:string){
  this.dataAS.getData(url).subscribe(data => {this.data = data})
}





ngOnInit(): void {
  this.getDataAsign('asignaturas')
}
}
