import {Component, EventEmitter, HostBinding, Input, OnInit, Output, ViewChild} from '@angular/core';

import {
  ITdDataTableColumn,
  ITdDataTableRowClickEvent,
  ITdDataTableSortChangeEvent, TdDataTableService,
  TdDataTableSortingOrder,
  TdDialogService, TdPagingBarComponent
} from '@covalent/core';
import {TaskStatus} from '../model/task-status';
import {TaskStatusServiceService} from '../services/task-status-service.service';

@Component({
  selector: 'app-task-status-list',
  templateUrl: './task-status-list.component.html',
  styleUrls: ['./task-status-list.component.scss'],
})
export class TaskStatusListComponent implements OnInit {

  @ViewChild(TdPagingBarComponent) pagingBar: TdPagingBarComponent;

  results: TaskStatus [];

  @Output()
  tasksForIdEmitter = new EventEmitter<TaskStatus[]>();

  columns: ITdDataTableColumn[] = [
    { name: 'taskId',  label: 'Task #' , numeric: true},
    { name: 'search', label: 'Input' },
    { name: 'category', label: 'Category'},
    { name: 'taskStatus', label: 'Status'}
  ];
  data: any[];
  basicData: any[];
  selectable: boolean = true;
  clickable: boolean = true;
  multiple: boolean = false;
  sortBy: string = 'search';
  resizableColumns: boolean = false;
  selectedRows: any[] = [];
  sortOrder: TdDataTableSortingOrder = TdDataTableSortingOrder.Descending;
  filteredData: any[];
  filteredTotal: number ;
  searchTerm: string = '';
  fromRow: number = 1;
  currentPage: number = 1;
  pageSize: number = 10;

  constructor(private taskStatusService: TaskStatusServiceService,
              private _dataTableService: TdDataTableService,
              private _dialogService: TdDialogService) { }

  async ngOnInit() {
    this.updatePage();
    this.data = await this.taskStatusService.getTaskResults().toPromise();
    this.basicData = this.data.slice(0, 10);
    this.filter();
  }
  async filter(): Promise<void> {
    let newData: any[] = this.data;
    let excludedColumns: string[] = await this.columns
        .filter((column: ITdDataTableColumn) => {
          return ((column.filter === undefined && column.hidden === true) ||
              (column.filter !== undefined && column.filter === false));
        }).map((column: ITdDataTableColumn) => {
          return column.name;
        });
    newData = await this._dataTableService.filterData(newData, this.searchTerm, true, excludedColumns);
    this.filteredTotal = newData.length;
    newData = await this._dataTableService.sortData(newData, this.sortBy, this.sortOrder);
    newData = await this._dataTableService.pageData(newData, this.fromRow, this.currentPage * this.pageSize);
    this.filteredData = newData;
  }

  sort(sortEvent: ITdDataTableSortChangeEvent): void {
    this.sortBy = sortEvent.name;
    this.sortOrder = sortEvent.order;
    this.filter();
  }

  updatePage() {
    return this.taskStatusService.getTaskResults()
        .subscribe((taskStatuses) => this.results = taskStatuses);
  }

  showAlert(event: any) {
    console.log(event.row.taskId);
    this.taskStatusService.getTaskForId(event.row.taskId)
        .subscribe((tasksForId) => this.tasksForIdEmitter.emit(tasksForId));
  }

  search(searchTerm: string): void {
    this.searchTerm = searchTerm;
    this.pagingBar.navigateToPage(1);
    this.filter();
  }
}
