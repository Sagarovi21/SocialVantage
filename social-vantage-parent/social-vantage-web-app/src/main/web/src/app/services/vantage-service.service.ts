import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Datainput} from '../model/datainput';
import {environment} from '../../environments/environment';
import {ResponseOutput} from '../model/response-output';

@Injectable({
  providedIn: 'root'
})
export class VantageServiceService {

  constructor(private http: HttpClient) { }

  getSearchResults(input: Datainput): Observable<ResponseOutput> {
    return this.http.post<ResponseOutput>(environment.apiUrl, input);
  }
}
