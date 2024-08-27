import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive} from '@angular/router';
import { ApiService } from '../../../../services/api.service';

@Component({
  selector: 'app-user-side',
  standalone: true,
  imports: [
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './user-side.component.html',
  styleUrl: './user-side.component.css'
})
export class UserSideComponent {
  constructor(private services:ApiService){}

  onLogout(){
    this.services.Logout()
  }


}
