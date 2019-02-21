import {Component, OnInit, ViewChild, AfterViewInit} from '@angular/core';
import {TaskStatus} from '../model/task-status';

@Component({
  selector: 'app-side-card',
  templateUrl: './side-card.component.html',
  styleUrls: ['./side-card.component.scss']
})
export class SideCardComponent implements OnInit , AfterViewInit {

  taskListForId: TaskStatus[];
  constructor() { }

  ngOnInit() {

  }

  ngAfterViewInit() {

  }
  openPrompt(row: any, name: string): void {

  }

  refresh() {

  }

  onMoreTask(taskStatusList: TaskStatus[]) {
    this.taskListForId = taskStatusList;
  }
}
