import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { NewBoardComponent } from '../new-board/new-board.component';

@Component({
  selector: 'app-accessible-boards',
  templateUrl: './accessible-boards.component.html',
  styleUrls: ['./accessible-boards.component.css']
})
export class AccessibleBoardsComponent implements OnInit {

  constructor(private dialog: MatDialog) { }

  ngOnInit(): void {
  }

  public newBoard(): void {
    this.dialog.open(NewBoardComponent, {
      width: '500px',
    });
  }
}
