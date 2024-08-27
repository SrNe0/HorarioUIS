import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

import { Router, RouterLink, RouterLinkActive} from '@angular/router';
import { ApiService } from '../../services/api.service';


@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent{
  
  constructor(private router:Router, private services: ApiService){}

  onLogout(){
    this.services.Logout()
  }

}
