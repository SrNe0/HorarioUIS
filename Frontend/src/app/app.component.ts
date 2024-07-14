import { Component} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import { HeaderComponent } from './components/header/header.component';
import { filter } from 'rxjs';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet, 
    RouterLink, 
    RouterLinkActive, 
    HeaderComponent,
    CommonModule
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent{
  title ="Frontend"
}
