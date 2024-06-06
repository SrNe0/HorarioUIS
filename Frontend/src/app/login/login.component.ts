import { Component, OnInit } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { ApiService } from '../services/api/api.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit{
  constructor(private dataAS:ApiService){}

  data:any[] = [];
  acces:boolean = false;

  user = new FormControl('', Validators.required);
  password = new FormControl('', Validators.required);

  private getDataUsers(url:string){
    this.dataAS.getData(url).subscribe(data => {
      this.data = data;
    })
  }

  ngOnInit(): void {
    this.getDataUsers('usuarios')
  }

}
