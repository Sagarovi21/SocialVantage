import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TaskStatusListComponent } from './task-status-list.component';

describe('TaskStatusListComponent', () => {
  let component: TaskStatusListComponent;
  let fixture: ComponentFixture<TaskStatusListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TaskStatusListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskStatusListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
