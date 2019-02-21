import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {TaskStatus} from '../model/task-status';

@Injectable({
  providedIn: 'root'
})
export class TaskStatusServiceService {

  constructor(private http: HttpClient) { }

  getTaskResults(): Observable<TaskStatus[]> {
    return this.http.get<TaskStatus[]>(environment.apiTaskUrl);
  }
  getTaskForId(taskId: number): Observable<TaskStatus[]> {
    return this.http.get<TaskStatus[]>(environment.apiTaskIdUrl + '/' + taskId);
  }
}
