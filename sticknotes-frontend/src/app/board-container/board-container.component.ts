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
    const dialogRef = this.dialog.open(NewBoardComponent, {
      width: '500px',
      data: { currentTitle: this.sidenavBoardData.title, boardId: this.sidenavBoardData.id }
    });
    dialogRef.afterClosed().subscribe(updatedBoardTitle => {
      if (updatedBoardTitle) {
        this.sidenavBoardData.title = updatedBoardTitle;
        this.newTitle = updatedBoardTitle;
      }
    });
  }

  public getBoardCreatedDate(): Date {
    if (this.sidenavBoardData) {
      return new Date(Number(this.sidenavBoardData.creationDate));
    }
  }
}
