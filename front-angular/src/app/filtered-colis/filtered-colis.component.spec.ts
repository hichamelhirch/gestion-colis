import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FilteredColisComponent } from './filtered-colis.component';

describe('FilteredColisComponent', () => {
  let component: FilteredColisComponent;
  let fixture: ComponentFixture<FilteredColisComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FilteredColisComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(FilteredColisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
