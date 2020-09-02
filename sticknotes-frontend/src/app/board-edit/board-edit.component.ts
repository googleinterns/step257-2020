import { Component, OnInit, Inject } from '@angular/core';
import { Validators, FormGroup, FormControl } from '@angular/forms';
import { onlySpacesValidator } from '../utility/util';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BoardApiService } from '../services/board-api.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { BoardData } from '../interfaces';

@Component({
  selector: 'app-board-edit',
  templateUrl: './board-edit.component.html',
  styleUrls: ['./board-edit.component.css']
})
export class BoardEditComponent implements OnInit {
  public sendingData = false;
  public editBoardForm = new FormGroup({
    boardTitle: new FormControl('', [
      Validators.required,
      onlySpacesValidator
    ]),
    cols: new FormControl('', [
      Validators.required,
      Validators.min(1),
      Validators.max(20)
    ]),
    rows: new FormControl('', [
      Validators.required,
      Validators.min(1),
      Validators.max(20)
    ])
  });
  constructor(
    @Inject(MAT_DIALOG_DATA) private boardData: BoardData, 
    private dialogRef: MatDialogRef<BoardEditComponent>,
    private boardApiService: BoardApiService,
    private snackBar: MatSnackBar) { }

  ngOnInit(): void {
    // prepopulate board data values in the formgroup
    // boardData is sent by caller component
    this.editBoardForm.controls.boardTitle.setValue(this.boardData.title);
    this.editBoardForm.controls.rows.setValue(this.boardData.rows);
    this.editBoardForm.controls.cols.setValue(this.boardData.cols);
  }

  /**
   * Sends the updated board data to the server
   */
  public updateBoard() {
    if (this.editBoardForm.valid) {
      this.sendingData = true;
      const updatePayload: BoardData = {
        title: this.editBoardForm.controls.boardTitle.value,
        cols: this.editBoardForm.controls.cols.value,
        rows: this.editBoardForm.controls.rows.value,
        id: this.boardData.id,
        backgroundImg: this.boardData.backgroundImg,
        creationDate: this.boardData.creationDate
      }
      // send data to the server
      this.boardApiService.updateBoard(updatePayload).subscribe(() => {
        // if update successfully, return updated data back to the caller component
        this.dialogRef.close(updatePayload);
        this.snackBar.open("Successfully updated!", "Ok", {
          duration: 2000,
        });
      }, err => {
        // if error occurred, display Material Snackbar with error message for 2 seconds
        this.dialogRef.close();
        this.snackBar.open("Error occurred while updating the board", "Ok", {
          duration: 2000,
        });
      });
    }
  }
}
