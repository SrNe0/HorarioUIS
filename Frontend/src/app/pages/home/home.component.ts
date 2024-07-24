import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

import { Router, RouterLink, RouterLinkActive} from '@angular/router';


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
  
  constructor(private router:Router){}

  onClickLogout(){
    localStorage.removeItem('token_user')
    this.router.navigate(['/login'])
  }


}
