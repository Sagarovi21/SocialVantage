import {Component, Input, OnInit} from '@angular/core';
import {Resultoutput} from '../model/resultoutput';
import {ITdDataTableColumn, TdDialogService} from '@covalent/core';

@Component({
  selector: 'app-result-table',
  templateUrl: './result-table.component.html',
  styleUrls: ['./result-table.component.scss']
})
export class ResultTableComponent implements OnInit {
  @Input()
  results: Resultoutput;

  columns: ITdDataTableColumn[] = [
    { name: 'rank',  label: 'Rank #' },
    { name: 'entity', label: 'Name' },
    { name: 'score', label: 'Score'}
  ];

  constructor(private _dialogService: TdDialogService) { }

  ngOnInit() {
  }

  openPrompt(row: any, name: string): void {
    this._dialogService.openPrompt({
      message: 'Enter comment?',
      value: row[name],
    }).afterClosed().subscribe((value: any) => {
      if (value !== undefined) {
        row[name] = value;
      }
    });
  }

}
