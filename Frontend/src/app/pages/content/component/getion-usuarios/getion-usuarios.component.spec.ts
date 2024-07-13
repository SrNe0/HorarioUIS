import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GetionUsuariosComponent } from './getion-usuarios.component';

describe('GetionUsuariosComponent', () => {
  let component: GetionUsuariosComponent;
  let fixture: ComponentFixture<GetionUsuariosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GetionUsuariosComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(GetionUsuariosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
