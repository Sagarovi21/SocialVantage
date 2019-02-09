import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Resultoutput} from '../model/resultoutput';
import {Datainput} from '../model/datainput';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class VantageServiceService {

  constructor(private http: HttpClient) { }

  getSearchResults(input: Datainput): Observable<Resultoutput> {
    return this.http.post<Resultoutput>(environment.apiUrl, input);
  }
}
