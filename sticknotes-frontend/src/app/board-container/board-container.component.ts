/**
 * A main view of the app, container that holds the board
 */
import { Component, OnInit } from '@angular/core';
import { MatDrawer } from '@angular/material/sidenav';
import { Router } from '@angular/router';
import { BoardData } from '../interfaces';
import { MatDialog } from '@angular/material/dialog';
import { BoardEditComponent } from '../board-edit/board-edit.component';

@Component({
  selector: 'app-board-container',
  templateUrl: './board-container.component.html',
  styleUrls: ['./board-container.component.css']
})
export class BoardContainerComponent implements OnInit {

  public iconName = 'menu';
  // used to receive data from the board
  // public sidenavBoardData: SidenavBoardData = null;
  // used to send updates to the board component
  public boardData: BoardData = null;
  constructor(private router: Router, private dialog: MatDialog) { }

  ngOnInit(): void {
  }

  // toggles the side menu, changes the icon name accordingly to the state
  public toggleMenu(drawer: MatDrawer): void {
    drawer.toggle();
    if (this.iconName === 'menu') {
      this.iconName = 'menu_open';
    } else {
      this.iconName = 'menu';
    }
  }

  public backToBoards(): void {
    this.router.navigateByUrl('/boards');
  }

  public receiveBoardData(boardData: BoardData): void {
    // when board data is emitted, add info about the board to the sidenav
    this.boardData = boardData;
  }

  public openEditBoardDialog() {
    const dialogRef = this.dialog.open(BoardEditComponent, {
      width: '500px',
      data: this.boardData
    });
    dialogRef.afterClosed().subscribe((data: BoardData) => {
      // if board was edited
      if (data) {
        // update local fields
        this.boardData = data;
      }
    });
  }

  public getBoardCreatedDate(): Date {
    if (this.boardData) {
      return new Date(Number(this.boardData.creationDate));
    }
  }
}
