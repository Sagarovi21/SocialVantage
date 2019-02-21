import { TestBed } from '@angular/core/testing';

import { TaskStatusServiceService } from './task-status-service.service';

describe('TaskStatusServiceService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: TaskStatusServiceService = TestBed.get(TaskStatusServiceService);
    expect(service).toBeTruthy();
  });
});
