import { Component, OnInit} from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit{
  currentRoute:string = '/usuario/servicios';

  constructor(private rout:Router){}


  ngOnInit() {
    this.rout.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.currentRoute = event.url;
      }
    });
  }
}
