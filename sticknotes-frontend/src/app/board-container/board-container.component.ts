/**
 * A main view of the app, container that holds the board
 */
import { Component, OnInit, Input } from '@angular/core';
import { MatDrawer } from '@angular/material/sidenav';
import { Router } from '@angular/router';
import { SidenavBoardData } from '../interfaces';
import { NewBoardComponent } from '../new-board/new-board.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-board-container',
  templateUrl: './board-container.component.html',
  styleUrls: ['./board-container.component.css']
})
export class BoardContainerComponent implements OnInit {

  public iconName = 'menu';
  public sidenavBoardData: SidenavBoardData = null;
  public newTitle: string = null;
  constructor(private router: Router, private dialog: MatDialog) { }

  ngOnInit(): void {
  }

  /**
   * Opens/closes the side menu, changes the icon accordingly to the state
   */
  public toggleMenu(drawer: MatDrawer): void {
    drawer.toggle();
    if (this.iconName === 'menu') {
      this.iconName = 'menu_open';
    } else {
      this.iconName = 'menu';
    }
  }

  /**
   * Navigates user to the main menu
   */
  public backToBoards(): void {
    this.router.navigateByUrl('/boards');
  }

  /**
   * Listens for the @Output Board of the board.component.ts
   */
  public receiveBoardData(sidenavBoardData: SidenavBoardData): void {
    // when board data is emitted, add info about the board to the sidenav
    this.sidenavBoardData = sidenavBoardData;
  }

  /**
   * Opens edit board dialog by opening the NewBoardComponent in "edit" mode
   * When editing is done, receives the edited board and updates the board data
   */
  public openEditBoardDialog() {
    const dialogRef = this.dialog.open(NewBoardComponent, {
      width: '500px',
      data: {currentTitle: this.sidenavBoardData.title, boardId: this.sidenavBoardData.id}
    });
    dialogRef.afterClosed().subscribe(updatedBoardTitle => {
      this.sidenavBoardData.title = updatedBoardTitle;
      this.newTitle = updatedBoardTitle;
    });
  }

  /**
   * Converts a timestamp received from server to the TS Date object
   */
  public getBoardCreatedDate(): Date {
    if (this.sidenavBoardData) {
      return new Date(Number(this.sidenavBoardData.creationDate));
    }
  }
}
