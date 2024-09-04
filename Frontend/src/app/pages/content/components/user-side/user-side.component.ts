import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive} from '@angular/router';
import { ApiService } from '../../../../services/api.service';
import { DragdropComponent } from '../../../../components/dragdrop/dragdrop.component';

@Component({
  selector: 'app-user-side',
  standalone: true,
  imports: [
    RouterLink,
    RouterLinkActive,
    DragdropComponent
],
  templateUrl: './user-side.component.html',
  styleUrl: './user-side.component.css'
})
export class UserSideComponent {
  constructor(private services: ApiService) {}
  showCsvUpload = false;

  toggleCsvUpload(): void {
    this.showCsvUpload = !this.showCsvUpload;
  }

  onLogout() {
    this.services.Logout();
  }

  onFileUploaded(file: File): void {
    console.log('Archivo subido:', file);
    this.showCsvUpload = false; 
  }

  closeCsvUpload(): void {
    this.showCsvUpload = false;
  }
}
