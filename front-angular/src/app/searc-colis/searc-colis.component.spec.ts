import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearcColisComponent } from './searc-colis.component';

describe('SearcColisComponent', () => {
  let component: SearcColisComponent;
  let fixture: ComponentFixture<SearcColisComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SearcColisComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SearcColisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
