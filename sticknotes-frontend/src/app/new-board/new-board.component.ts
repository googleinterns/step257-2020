import { Component, OnInit, Inject } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { onlySpacesValidator } from '../utility/util';
import { BoardApiService } from '../services/board-api.service';
import { State } from '../enums/state.enum';

@Component({
  selector: 'app-new-board',
  templateUrl: './new-board.component.html',
  styleUrls: ['./new-board.component.css']
})
export class NewBoardComponent implements OnInit {
  // flag used to disable submit button when the component in the process of sending data
  public sendingData = false;
  public submitButton: string;
  private mode: State;
  private boardId: string;
  // A form group used in the .html, currently has only board title, which is the only required field to create a new board
  public newBoardForm = new FormGroup({
    boardTitle: new FormControl('', [
      // boardTitle field uses two validators
      // checks that field is not empty
      Validators.required,
      // checks if field has anything else from spaces only
      onlySpacesValidator
    ])
  });
  constructor(private router: Router,
    private dialogRef: MatDialogRef<NewBoardComponent>,
    private boardApiService: BoardApiService,
    @Inject(MAT_DIALOG_DATA) private data: any) { }

  /**
   * Initializes the local component data
   */
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

  /**
   * Creates a new board with the given name.
   */
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

  /** 
   * Updates board title, later it will handle background image updates as well.
   */
  private updateBoard(): void {
    if (this.newBoardForm.valid) {
      const boardTitle = this.newBoardForm.controls.boardTitle.value;
      this.sendingData = true;
      this.boardApiService.updateBoardTitle(this.boardId, boardTitle).subscribe(updatedBoard => {
        this.dialogRef.close(boardTitle);
      });
    }
  }

  /**
   * Called when user clicks "submit" button. Depending on the current state of the component, may either
   * update an existing board or create new one.
   */
  public handleSubmit(): void {
    if (this.mode === State.EDIT) {
      this.updateBoard();
    } else {
      this.createNewBoard();
    }
  }
}
