import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { NewBoardComponent } from '../new-board/new-board.component';

@Component({
  selector: 'app-boards-list',
  templateUrl: './boards-list.component.html',
  styleUrls: ['./boards-list.component.css']
})
export class BoardsListComponent implements OnInit {

  constructor(private dialog: MatDialog) { }

  ngOnInit(): void {
  }

  /**
   * Opens a NewBoardComponent in a dialog in a "create" mode
   */
  public showNewBoardDialog(): void {
    this.dialog.open(NewBoardComponent, {
      width: '500px',
    });
  }
}
