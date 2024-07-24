import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { firstValueFrom } from 'rxjs';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterLink, ReactiveFormsModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  formulario: FormGroup;

  constructor(private services:ApiService, private router:Router){
    this.formulario = new FormGroup({
      username:   new FormControl('', [Validators.required]),
      password:   new FormControl('', [Validators.required])
    })
  }

  async onSubmit() {
    if (this.formulario.valid) {
      try {
        const response = await firstValueFrom(this.services.authenticateLogin(this.formulario.value));
        if (!response.error) {
          console.log('Login successful', response);
          localStorage.setItem('token_user', response.jwt)
          this.router.navigate(['/home']);
        }
      } catch (error:any) {
        console.error('Error during login:', error);
        alert(error.message);
      }
    }
  }

}