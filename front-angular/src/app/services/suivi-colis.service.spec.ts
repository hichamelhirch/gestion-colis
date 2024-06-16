import { TestBed } from '@angular/core/testing';

import { SuiviColisService } from './suivi-colis.service';

describe('SuiviColisService', () => {
  let service: SuiviColisService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SuiviColisService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
