import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../../services/api/api.service';

@Component({
  selector: 'app-gestuser',
  templateUrl: './gestuser.component.html',
  styleUrl: './gestuser.component.css'
})
export class GestuserComponent implements OnInit {
  url:string = 'usuarios'
  data:any[] = [];

  constructor(private dataAS:ApiService){}

  private getDataUsers(url:string){
    this.dataAS.getData(url).subscribe(data => {this.data = data})
  }

  deleteDataUserId(url: string, id: number) {
    this.dataAS.deleteDataId(url, id).subscribe(() => {
      console.log(`Registro con id ${id} eliminado`);
      this.getDataUsers(this.url); 
    }, error => {
      console.error('Error al eliminar el registro', error);
    });
  }

  ngOnInit(): void {
    this.getDataUsers(this.url)
  }
}
