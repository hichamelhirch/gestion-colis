import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateColisBulkComponent } from './create-colis-bulk.component';

describe('CreateColisBulkComponent', () => {
  let component: CreateColisBulkComponent;
  let fixture: ComponentFixture<CreateColisBulkComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreateColisBulkComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CreateColisBulkComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
