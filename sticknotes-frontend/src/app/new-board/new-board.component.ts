// Copyright 2020 Google LLC

import { Component } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatDialogRef } from '@angular/material/dialog';
import { noSpacesValidator } from '../utility/util';
import { BoardApiService } from '../services/board-api.service';

@Component({
  selector: 'app-new-board',
  templateUrl: './new-board.component.html',
  styleUrls: ['./new-board.component.css']
})
export class NewBoardComponent {
  public sendingData = false;
  public newBoardForm = new FormGroup({
    boardTitle: new FormControl('', [
      Validators.required,
      noSpacesValidator
    ])
  });

  constructor(private router: Router,
    private dialogRef: MatDialogRef<NewBoardComponent>,
    private boardApiService: BoardApiService) { }

  // creates a new board with the given name
  public createNewBoard(): void {
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
}
