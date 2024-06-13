import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ColisLabelComponent } from './colis-label.component';

describe('ColisLabelComponent', () => {
  let component: ColisLabelComponent;
  let fixture: ComponentFixture<ColisLabelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ColisLabelComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ColisLabelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
