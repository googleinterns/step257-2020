/**
 * A main view of the app, container that holds the board
 */
import { Component, OnInit } from '@angular/core';
import { MatDrawer } from '@angular/material/sidenav';
import { Router } from '@angular/router';
import { BoardData } from '../interfaces';
import { MatDialog } from '@angular/material/dialog';
import { BoardEditComponent } from '../board-edit/board-edit.component';
import { Note } from '../interfaces';
import { FormControl, Validators } from '@angular/forms';
import { BoardApiService } from '../services/board-api.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-board-container',
  templateUrl: './board-container.component.html',
  styleUrls: ['./board-container.component.css']
})
export class BoardContainerComponent implements OnInit {
  public iconName = 'menu';
  // used to receive data from the board and to send updates to the board component
  public boardData: BoardData = null;
  public translateFormControl = new FormControl('', [Validators.required]);
  public translatedNotes: Note[] = null;
  // languages to which notes can be translated
  public translateLanguages = [
    { value: "ua", viewValue: "Українська" },
    { value: "pl", viewValue: "Polski" },
    { value: "ro", viewValue: "Română" },
    { value: "hr", viewValue: "Hrvatski" },
    { value: "zn", viewValue: "中文" },
    { value: "en", viewValue: "English" },
    { value: "te", viewValue: "తెలుగు" }
  ];
  constructor(private router: Router, private dialog: MatDialog, private boardApiService: BoardApiService, private snackBar: MatSnackBar) { }

  ngOnInit(): void {
    // sort languages
    this.translateLanguages.sort((a, b) => {
      if (a.value > b.value) {
        return 1;
      }
      if (a.value < b.value) {
        return -1;
      }
      return 0;
    });
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
  public receiveBoardData(boardData: BoardData): void {
    // when board data is emitted, add info about the board to the sidenav
    this.boardData = boardData;
  }

  /**
   * Opens edit board dialog by opening the NewBoardComponent in "edit" mode
   * When editing is done, receives the edited board and updates the board data
   */
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

  /**
   * Converts a timestamp received from server to the TS Date object
   */
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
      // get the target language
      const targetLanguage = this.translateFormControl.value;
      this.boardApiService.translateNotesOfBoard(this.boardData.id, targetLanguage).subscribe(notes => {
        this.translatedNotes = notes;
      }, err => {
        // display error snackbar for 2 seconds
        this.snackBar.open("Server error occurred when translating notes", "Ok", {
          duration: 2000,
        });
      })
    }
  }
}
