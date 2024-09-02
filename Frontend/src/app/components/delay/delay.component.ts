import { Component } from '@angular/core';

@Component({
  selector: 'app-delay',
  standalone: true,
  imports: [],
  templateUrl: './delay.component.html',
  styleUrl: './delay.component.css'
})
export class DelayComponent {
  show:boolean = false;
}
