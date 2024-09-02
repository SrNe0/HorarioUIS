import { Component, OnInit, ViewContainerRef } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { firstValueFrom } from 'rxjs';
import { DelayService } from '../../services/delay.service';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    RouterLink, 
    ReactiveFormsModule, 
    FormsModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit{
  formulario: FormGroup;

  constructor(
    private services:ApiService, 
    private router:Router,
    private delayService: DelayService,
    private VCR: ViewContainerRef
  ){
    this.formulario = new FormGroup({
      username:   new FormControl('', [Validators.required]),
      password:   new FormControl('', [Validators.required])
    })
  }

  ngOnInit(): void {
    this.delayService.setViewContainerRef(this.VCR)
  }

  async onSubmit() {
    this.delayService.applyDelayWithLoading(1000).subscribe(async () => {
      if (this.formulario.valid) {
        try {
          const response = await firstValueFrom(this.services.authenticateLogin(this.formulario.value));
          if (!response.error) {
            alert('Login successful');
            if (typeof window !== 'undefined') {
              localStorage.setItem('token_user', response.jwt);
            }
            this.router.navigate(['/home']);
          }
        } catch (error: any) {
          console.error('Error during login:', error);
          alert(error.message);
        }
      }
    });
  }

}