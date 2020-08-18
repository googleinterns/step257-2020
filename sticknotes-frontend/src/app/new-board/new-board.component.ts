import { Component, OnInit, Inject } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { noSpacesValidator } from '../utility/util';
import { BoardApiService } from '../services/board-api.service';

@Component({
  selector: 'app-new-board',
  templateUrl: './new-board.component.html',
  styleUrls: ['./new-board.component.css']
})
export class NewBoardComponent implements OnInit {
  public sendingData = false;
  public submitButton: string;
  private mode: 'create' | 'edit';
  private boardKey: string;
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
      this.boardKey = this.data.boardKey;
      this.submitButton = 'Update';
      this.mode = 'edit';
    } else {
      this.submitButton = 'Create';
      this.mode = 'create';
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
        this.router.navigateByUrl(`board/${newBoard.key}`);
      });
    }
  }

  // updates board title, later it will handle background image updates as well
  private updateBoard(): void {
    if (this.newBoardForm.valid) {
      const boardTitle = this.newBoardForm.controls.boardTitle.value;
      this.sendingData = true;
      this.boardApiService.updateBoardTitle(this.boardKey, boardTitle).subscribe(updatedBoard => {
        this.dialogRef.close(boardTitle);
      });
    }
  }

  public handleSubmit(): void {
    if (this.mode === 'edit') {
      this.updateBoard();
    } else {
      this.createNewBoard();
    }
  }
}
