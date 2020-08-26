/**
 * A main view of the app, container that holds the board
 */
import { Component, OnInit, Input } from '@angular/core';
import { MatDrawer } from '@angular/material/sidenav';
import { Router } from '@angular/router';
import { SidenavBoardData, Note } from '../interfaces';
import { NewBoardComponent } from '../new-board/new-board.component';
import { MatDialog } from '@angular/material/dialog';
import { FormControl } from '@angular/forms';
import { BoardApiService } from '../services/board-api.service';

@Component({
  selector: 'app-board-container',
  templateUrl: './board-container.component.html',
  styleUrls: ['./board-container.component.css']
})
export class BoardContainerComponent implements OnInit {

  public iconName = 'menu';
  public sidenavBoardData: SidenavBoardData = null;
  public newTitle: string = null;
  public translateFormControl = new FormControl('', []);
  public translatedNotes: Note[] = null;
  // language to which notes can be translated
  public translateLanguages = [
    { value: "ua", viewValue: "Українська" },
    { value: "pl", viewValue: "Polski" },
    { value: "ro", viewValue: "Română" },
    { value: "hr", viewValue: "Hrvatski" },
    { value: "zn", viewValue: "中文" },
    { value: "en", viewValue: "English" },
    { value: "te", viewValue: "తెలుగు"}
  ];
  constructor(private router: Router, private dialog: MatDialog, private boardApiService: BoardApiService) { }

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
      this.sidenavBoardData.title = updatedBoardTitle;
      this.newTitle = updatedBoardTitle;
    });
  }

  public getBoardCreatedDate(): Date {
    if (this.sidenavBoardData) {
      return new Date(Number(this.sidenavBoardData.creationDate));
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
      this.boardApiService.translateNotesOfBoard(this.sidenavBoardData.id, targetLanguage).subscribe(notes => {
        this.translatedNotes = notes;
      })
    }
  }
}
