// Copyright 2020 Google LLC

import { Component, OnInit, Inject } from '@angular/core';
import { Validators, FormGroup, FormControl } from '@angular/forms';
import { noSpacesValidator } from '../utility/util';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BoardApiService } from '../services/board-api.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { BoardDescription } from '../interfaces';
import { SharedBoardService } from '../services/shared-board.service';

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
      noSpacesValidator
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
    @Inject(MAT_DIALOG_DATA) private boardData: BoardDescription,
    private dialogRef: MatDialogRef<BoardEditComponent>,
    private boardApiService: BoardApiService,
    private snackBar: MatSnackBar,
    private sharedBoard: SharedBoardService) { }

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
  public updateBoard(): void {
    if (this.editBoardForm.valid) {
      this.sendingData = true;
      const updatePayload: BoardDescription = {
        title: this.editBoardForm.controls.boardTitle.value,
        cols: this.editBoardForm.controls.cols.value,
        rows: this.editBoardForm.controls.rows.value,
        id: this.boardData.id,
        backgroundImg: this.boardData.backgroundImg,
        creationDate: this.boardData.creationDate,
        creator: this.boardData.creator
      };
      // send data to the server
      this.boardApiService.updateBoard(updatePayload).subscribe(updatedBoard => {
        // if update successfull, send updated data to the shared board and close dialog
        this.sharedBoard.updateBoard(updatedBoard);
        this.dialogRef.close();
        this.snackBar.open('Successfully updated!', 'Ok', {
          duration: 2000,
        });
      }, err => {
        // if error occurred, display Material Snackbar with error message for 2 seconds
        this.dialogRef.close();
        this.snackBar.open('Error occurred while updating the board', 'Ok', {
          duration: 2000,
        });
      });
    }
  }
}
