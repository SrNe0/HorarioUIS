import { Component, EventEmitter, Output, Input, OnInit, ViewContainerRef} from '@angular/core';
import { DelayService } from '../../services/delay.service';

@Component({
  selector: 'app-confirmacion',
  standalone: true,
  imports: [],
  templateUrl: './confirmacion.component.html',
  styleUrl: './confirmacion.component.css'
})
export class ConfirmacionComponent implements OnInit{
  constructor(   
    private delayService: DelayService,
    private VCR: ViewContainerRef
  ){}
  
  @Input() message: string = "";
  @Input() nombreObjeto: string = "";

  @Output() confirm = new EventEmitter<boolean>();

  ngOnInit(): void {
    this.delayService.setViewContainerRef(this.VCR)
  }

  onConfirm() {
    this.delayService.applyDelayWithLoading(1000).subscribe(() => {
      this.confirm.emit(true);
    })
  }

  onCancel() {
    this.delayService.applyDelayWithLoading(1000).subscribe(() => {
      this.confirm.emit(false);
    })
  }
}
