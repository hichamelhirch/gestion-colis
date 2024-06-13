import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StockColisComponent } from './stock-colis.component';

describe('StockColisComponent', () => {
  let component: StockColisComponent;
  let fixture: ComponentFixture<StockColisComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [StockColisComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(StockColisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
