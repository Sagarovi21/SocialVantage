import { TestBed } from '@angular/core/testing';

import { VantageServiceService } from './vantage-service.service';

describe('VantageServiceService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: VantageServiceService = TestBed.get(VantageServiceService);
    expect(service).toBeTruthy();
  });
});
