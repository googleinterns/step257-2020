/**
 * A main view of the app, container that holds the board
 */
import { Component, OnInit, Input } from '@angular/core';
import { MatDrawer } from '@angular/material/sidenav';
import { Router } from '@angular/router';
import { SidenavBoardData, BoardUpdateData, Board } from '../interfaces';
import { NewBoardComponent } from '../new-board/new-board.component';
import { MatDialog } from '@angular/material/dialog';
import { BoardEditComponent } from '../board-edit/board-edit.component';

@Component({
  selector: 'app-board-container',
  templateUrl: './board-container.component.html',
  styleUrls: ['./board-container.component.css']
})
export class BoardContainerComponent implements OnInit {

  public iconName = 'menu';
  public sidenavBoardData: SidenavBoardData = null;
  public updatedBoardData: BoardUpdateData = null;
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

  public receiveBoardData(sidenavBoardData: SidenavBoardData): void {
    // when board data is emitted, add info about the board to the sidenav
    this.sidenavBoardData = sidenavBoardData;
  }

  public openEditBoardDialog() {
    const dialogRef = this.dialog.open(BoardEditComponent, {
      width: '500px',
      data: this.sidenavBoardData as BoardUpdateData
    });
    dialogRef.afterClosed().subscribe((data: BoardUpdateData) => {
      // if board was edited
      if (data) {
        // update local fields
        this.sidenavBoardData.title = data.title;
        this.sidenavBoardData.cols = data.cols;
        this.sidenavBoardData.rows = data.rows;
        // send update to the board component
        this.updatedBoardData = data;
      }
    });
  }

  public getBoardCreatedDate(): Date {
    if (this.sidenavBoardData) {
      return new Date(Number(this.sidenavBoardData.creationDate));
    }
  }
}
