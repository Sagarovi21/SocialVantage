import { Component, OnInit } from '@angular/core';
import {VantageServiceService} from '../services/vantage-service.service';
import {Datainput} from '../model/datainput';
import {Resultoutput} from '../model/resultoutput';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  inputData: Datainput;
  output: Resultoutput;
  selectedcategory: string;
  inputText: string;
  constructor(private vantageService: VantageServiceService) { }

  ngOnInit() {
    this.selectedcategory = 'reviews';

  }

    submit() {

      this.inputData = new Datainput(this.inputText, this.selectedcategory);
      this.vantageService.getSearchResults(this.inputData)
          .subscribe(output => this.output = output);
    }
}
