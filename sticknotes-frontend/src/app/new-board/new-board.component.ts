import { Component, OnInit, Inject } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { noSpacesValidator } from '../utility/util';
import { BoardApiService } from '../services/board-api.service';
import { State } from '../enums/state.enum';

@Component({
  selector: 'app-new-board',
  templateUrl: './new-board.component.html',
  styleUrls: ['./new-board.component.css']
})
export class NewBoardComponent implements OnInit {
  public sendingData = false;
  public submitButton: string;
  private mode: State;
  private boardId: string;
  public newBoardForm = new FormGroup({
    boardTitle: new FormControl('', [
      Validators.required,
      noSpacesValidator
    ])
  });
  constructor(private router: Router,
              private dialogRef: MatDialogRef<NewBoardComponent>,
              private boardApiService: BoardApiService,
              @Inject(MAT_DIALOG_DATA) private data: any) { }

  ngOnInit(): void {
    // if component in edit mode, prepopulate form data
    if (this.data && this.data.currentTitle) {
      this.newBoardForm.controls.boardTitle.setValue(this.data.currentTitle);
      this.boardId = this.data.boardId;
      this.submitButton = 'Update';
      this.mode = State.EDIT;
    } else {
      this.submitButton = 'Create';
      this.mode = State.CREATE;
    }
  }

  // creates a new board with the given name
  private createNewBoard(): void {
    if (this.newBoardForm.valid) {
      // to create a board we only need a title
      const boardTitle = this.newBoardForm.controls.boardTitle.value;
      this.sendingData = true;
      this.boardApiService.createBoard(boardTitle).subscribe(newBoard => {
        this.dialogRef.close();
        this.router.navigateByUrl(`board/${newBoard.id}`);
      });
    }
  }

  // updates board title, later it will handle background image updates as well
  private updateBoard(): void {
    if (this.newBoardForm.valid) {
      const boardTitle = this.newBoardForm.controls.boardTitle.value;
      this.sendingData = true;
      this.boardApiService.updateBoardTitle(this.boardId, boardTitle).subscribe(updatedBoard => {
        this.dialogRef.close(boardTitle);
      });
    }
  }

  public handleSubmit(): void {
    if (this.mode === State.EDIT) {
      this.updateBoard();
    } else {
      this.createNewBoard();
    }
  }
}
