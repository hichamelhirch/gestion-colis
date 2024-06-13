import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditColisComponent } from './edit-colis.component';

describe('EditColisComponent', () => {
  let component: EditColisComponent;
  let fixture: ComponentFixture<EditColisComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditColisComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(EditColisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
