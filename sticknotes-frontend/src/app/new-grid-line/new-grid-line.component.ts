import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { BoardGridLineType } from '../enums/board-grid-line-type.enum';
import { BoardGridLine } from '../interfaces';
import { BoardApiService } from '../services/board-api.service';
import { SharedBoardService } from '../services/shared-board.service';

/**
 * Component that is used to display an input field when user creates title to the row/column
 */
@Component({
  selector: 'app-new-grid-line',
  templateUrl: './new-grid-line.component.html',
  styleUrls: ['./new-grid-line.component.css']
})
export class NewGridLineComponent implements OnInit {
  // form which holds the input field
  public newLineFormGroup = new FormGroup({
    title: new FormControl('', [Validators.required]),
    width: new FormControl(1,[Validators.min(1)]) // minimal width is 1
  });
  // values passed by caller component
  private rangeStart: number;
  public type: BoardGridLineType;
  private boardId: string;
  // flag to disable "submit" button
  public submitDisabled = false;

  constructor(private dialogRef: MatDialogRef<NewGridLineComponent>, 
    private sharedBoard: SharedBoardService,
    private boardApiService: BoardApiService,
    private snackbar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) callersData) { 
      this.rangeStart = callersData.rangeStart;
      this.type = callersData.type;
      this.boardId = callersData.boardId;
      // TODO set max width validator based on the width/height of the board
    }

  ngOnInit(): void {
  }

  /**
   * Checks if form is valid, makes a request to server, updates the shared board
   */
  public submit(): void {
    if (this.newLineFormGroup.valid && !this.submitDisabled) {
      this.submitDisabled = true;
      const lineTitle = this.newLineFormGroup.controls.title.value;
      const width = this.newLineFormGroup.controls.width.value;
      const linePayload = {
        rangeStart: this.rangeStart,
        rangeEnd: this.rangeStart + width,
        title: lineTitle,
        type: this.type
      };
      this.boardApiService.createBoardGridLine(linePayload, this.boardId).subscribe(data => {
        // successfully created, close the dialog and update shared board
        this.sharedBoard.addGridLine(data);
        this.dialogRef.close();
      }, err => {
        // error occurred, open snackbar to inform the user
        this.snackbar.open("Error occurred", "Ok");
        // close dialog anyway
        this.dialogRef.close();
      });
    }
  }
}
