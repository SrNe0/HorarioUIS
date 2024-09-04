import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-dragdrop',
  standalone: true,
  imports: [],
  templateUrl: './dragdrop.component.html',
  styleUrl: './dragdrop.component.css'
})
export class DragdropComponent {
  dragging = false;
  fileName: string | null = null;

  @Output() fileUploaded = new EventEmitter<File>();
  @Output() closed = new EventEmitter<void>();  // Nuevo Output para el cierre

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.dragging = true;
  }

  onDragLeave(event: DragEvent): void {
    this.dragging = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.dragging = false;
    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      this.handleFile(files[0]);
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.handleFile(input.files[0]);
    }
  }

  handleFile(file: File): void {
    if (file.type === 'text/csv') {
      this.fileName = file.name;
      this.fileUploaded.emit(file); // Emitir el archivo cargado
    } else {
      alert('Por favor, sube un archivo CSV válido.');
    }
  }

  closeDragDrop(): void {
    this.closed.emit(); // Emitir el evento de cierre
  }
}
