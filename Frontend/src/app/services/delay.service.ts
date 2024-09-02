import { Injectable, ViewContainerRef, ComponentRef } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay, finalize } from 'rxjs';
import { DelayComponent } from '../components/delay/delay.component';

@Injectable({
  providedIn: 'root'
})
export class DelayService {

  private viewContainerRef?: ViewContainerRef;

  constructor() {}

  public setViewContainerRef(vcr: ViewContainerRef) {
    this.viewContainerRef = vcr;
  }

  public applyDelayWithLoading(ms: number = 1000): Observable<void> {
    const loadingComponent = this.createLoadingComponent();

    return of(undefined).pipe(
      delay(ms),
      finalize(() => {
        this.destroyLoadingComponent(loadingComponent);
      })
    );
  }

  private createLoadingComponent(): ComponentRef<DelayComponent> | null {
    if (!this.viewContainerRef) {
      console.error('ViewContainerRef is not set. Please set it before calling applyDelayWithLoading.');
      return null;
    }

    const componentRef = this.viewContainerRef.createComponent(DelayComponent);
    componentRef.instance.show = true;
    return componentRef;
  }

  private destroyLoadingComponent(componentRef: ComponentRef<DelayComponent> | null): void {
    if (componentRef) {
      componentRef.instance.show = false;
      componentRef.destroy();
    }
  }
}
