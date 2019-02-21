import {Component, Input, OnInit} from '@angular/core';
import {TaskStatus} from '../model/task-status';
import {ITdDataTableColumn} from '@covalent/core';
import {TaskStatusServiceService} from '../services/task-status-service.service';

@Component({
  selector: 'app-task-status-details',
  templateUrl: './task-status-details.component.html',
  styleUrls: ['./task-status-details.component.scss']
})
export class TaskStatusDetailsComponent implements OnInit {

  @Input()
  taskDetails: TaskStatus[];


  columns: ITdDataTableColumn[] = [
    { name: 'taskId',  label: 'Task #' },
    { name: 'task', label: 'Task' },
    { name: 'pagesFound', label: 'Pages Found'},
    { name: 'pageCompleted', label: 'Pages Completed'},
    { name: 'commentsFound', label: '# Comments'},
    { name: 'taskStatus', label: 'Current Status'},
  ];
  constructor(private taskStatusService: TaskStatusServiceService) { }

  ngOnInit() {
  }

  updatePage() {
    if(this.taskDetails && this.taskDetails.length > 0) {
      this.taskStatusService.getTaskForId(this.taskDetails[0].taskId)
          .subscribe((taskStatuses) => this.taskDetails = taskStatuses);
    }
  }

}
