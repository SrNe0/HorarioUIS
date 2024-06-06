import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormchagesComponent } from './formchages.component';

describe('FormchagesComponent', () => {
  let component: FormchagesComponent;
  let fixture: ComponentFixture<FormchagesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FormchagesComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(FormchagesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
