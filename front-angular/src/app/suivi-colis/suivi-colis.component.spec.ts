import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SuiviColisComponent } from './suivi-colis.component';

describe('SuiviColisComponent', () => {
  let component: SuiviColisComponent;
  let fixture: ComponentFixture<SuiviColisComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SuiviColisComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SuiviColisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
