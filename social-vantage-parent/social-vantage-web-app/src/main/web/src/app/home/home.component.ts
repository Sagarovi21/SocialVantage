import { Component, OnInit } from '@angular/core';
import {VantageServiceService} from '../services/vantage-service.service';
import {Datainput} from '../model/datainput';
import {ResponseOutput} from '../model/response-output';
import {TaskStatusListComponent} from '../task-status-list/task-status-list.component';

@Component({
  providers: [TaskStatusListComponent],
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  inputData: Datainput;
  output: ResponseOutput;
  selectedcategory: string;
  inputText: string;
  constructor(private vantageService: VantageServiceService) { }

  ngOnInit() {
    this.selectedcategory = 'reviews';

  }

    submit() {

      this.inputData = new Datainput(this.inputText, this.selectedcategory);
      this.vantageService.getSearchResults(this.inputData)
          .subscribe(output => {
            this.output = output;
          });
    }
}
