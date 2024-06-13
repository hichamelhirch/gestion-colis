import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateColisIndividualComponent } from './create-colis-individual.component';

describe('CreateColisIndividualComponent', () => {
  let component: CreateColisIndividualComponent;
  let fixture: ComponentFixture<CreateColisIndividualComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreateColisIndividualComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CreateColisIndividualComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
