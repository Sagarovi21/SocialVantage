import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TaskStatusDetailsComponent } from './task-status-details.component';

describe('TaskStatusDetailsComponent', () => {
  let component: TaskStatusDetailsComponent;
  let fixture: ComponentFixture<TaskStatusDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TaskStatusDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskStatusDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
