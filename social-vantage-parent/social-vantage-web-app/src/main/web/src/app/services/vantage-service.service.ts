import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Resultoutput} from '../model/resultoutput';
import {Datainput} from '../model/datainput';

@Injectable({
  providedIn: 'root'
})
export class VantageServiceService {

  constructor(private http: HttpClient) { }

  getSearchResults(input: Datainput): Observable<Resultoutput> {
    // return this.http.post<Resultoutput>('https://socialvantagemiddleware.appcenter.ps.ac.uda.io/search', input);
    return this.http.post<Resultoutput>('http://localhost/search', input);
  }
}
