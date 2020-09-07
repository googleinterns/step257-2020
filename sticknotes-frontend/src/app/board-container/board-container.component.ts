/**
 * A main view of the app, container that holds the board
 */
import { Component } from '@angular/core';
import { MatDrawer } from '@angular/material/sidenav';
import { Router } from '@angular/router';
import { BoardData, UserBoardRole } from '../interfaces';
import { MatDialog } from '@angular/material/dialog';
import { BoardEditComponent } from '../board-edit/board-edit.component';
import { FormControl, Validators } from '@angular/forms';
import { Observable, forkJoin } from 'rxjs';
import { BoardUsersApiService } from '../services/board-users-api.service';
import { UserService } from '../services/user.service';
import { take } from 'rxjs/operators';

@Component({
  selector: 'app-board-container',
  templateUrl: './board-container.component.html',
  styleUrls: ['./board-container.component.css']
})
export class BoardContainerComponent {
  public iconName = 'menu';
  // used to receive data from the board and to send updates to the board component
  public boardData: BoardData = null;
  public translateFormControl = new FormControl('', [Validators.required]);
  public targetLanguage: string = null;
  // flag for storing user's permission to edit the board
  public canEditBoard = false;
  // languages to which notes can be translated
  public translateLanguages = [
    { value: "en", viewValue: "English" },
    { value: "hr", viewValue: "Hrvatski" },
    { value: "pl", viewValue: "Polski" },
    { value: "ro", viewValue: "Română" },
    { value: "te", viewValue: "తెలుగు" },
    { value: "uk", viewValue: "Українська" },
    { value: "zh", viewValue: "中文" },
  ];

  constructor(private router: Router, private dialog: MatDialog, private boardUsersApiService: BoardUsersApiService, private userService: UserService) { 
    forkJoin([
      this.userService.getUser().pipe(take(1)),
      // first emitted default value of behavior subject, second is actual rules
      this.boardUsersApiService.getBoardUsersSubject().pipe(take(2))
    ]).subscribe(([user, roles]) => {
      const role = roles.find(r => r.user.id === user.id);
      this.canEditBoard = (role && (role.role === 'OWNER' || role.role === 'ADMIN'));
    });
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
  /**
   * Requests server to translate board notes to a language specified in "translateFormControl".
   * Sends updated data to the board-component
   */
  public translateNotes(): void {
    if (this.translateFormControl.valid) {
      // get the target language and 
      // send target language to the board component
      this.targetLanguage = this.translateFormControl.value;
    }
  }

  get boardCreator() {
    if (this.boardData.creator.nickname != "---") {
      return this.boardData.creator.nickname;
    }
    return this.boardData.creator.email;
  }
}
