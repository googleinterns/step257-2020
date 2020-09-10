/**
 * A main view of the app, container that holds the board
 */
import { Component } from '@angular/core';
import { MatDrawer } from '@angular/material/sidenav';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { BoardEditComponent } from '../board-edit/board-edit.component';
import { FormControl, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { BoardUsersApiService } from '../services/board-users-api.service';
import { UserService } from '../services/user.service';
import { take } from 'rxjs/operators';
import { TranslateService } from '../services/translate.service';
import { SharedBoardService } from '../services/shared-board.service';

@Component({
  selector: 'app-board-container',
  templateUrl: './board-container.component.html',
  styleUrls: ['./board-container.component.css']
})
export class BoardContainerComponent {
  public boardData = null;
  public iconName = 'menu';
  public translateFormControl = new FormControl("original", [Validators.required]);

  // flag for storing user's permission to edit the board
  public canEditBoard = false;

  constructor(private router: Router,
    private dialog: MatDialog,
    private boardUsersApiService: BoardUsersApiService,
    private sharedBoard: SharedBoardService,
    private userService: UserService,
    public translateService: TranslateService) {
    forkJoin([
      this.userService.getUser().pipe(take(1)),
      // first emitted default value of behavior subject, second is actual rules
      this.boardUsersApiService.getBoardUsersSubject().pipe(take(2))
    ]).subscribe(([user, roles]) => {
      const role = roles.find(r => r.user.id === user.id);
      this.canEditBoard = (role && (role.role === 'OWNER' || role.role === 'ADMIN'));
    });

    // subscribe to board data changes
    this.sharedBoard.boardObservable().subscribe(board => {
      if (board) {
        this.boardData = {
          id: board.id,
          title: board.title,
          creationDate: board.creationDate,
          backgroundImg: board.backgroundImg,
          rows: board.rows,
          cols: board.cols,
          creator: board.creator
        };
      }
    });
  }

  /** 
   * toggles the side menu, changes the icon name accordingly to the state
   */
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

  /**
   * Opens edit board component in a dialog
   */
  public openEditBoardDialog() {
    this.dialog.open(BoardEditComponent, {
      data: this.boardData
    });
  }

  /**
   * Emits new language value to translate service
   */
  public translateNotes(): void {
    if (this.translateFormControl.valid) {
      // get the target language and 
      // send target language to the board component
      const targetLanguage = this.translateFormControl.value;
      // emit new translation value
      this.translateService.notesTargetLanguage.next(targetLanguage);
    }
  }

  get boardCreator() {
    if (this.boardData.creator.nickname != "---") {
      return this.boardData.creator.nickname;
    }
    return this.boardData.creator.email;
  }

  get boardCreatedDate(): Date {
    if (this.boardData) {
      return new Date(Number(this.boardData.creationDate));
    }
  }
}
