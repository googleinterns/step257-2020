import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-new-board',
  templateUrl: './new-board.component.html',
  styleUrls: ['./new-board.component.css']
})
export class NewBoardComponent implements OnInit {
  public newBoardForm = new FormGroup({
    boardName: new FormControl('', [
      Validators.required,
    ])
  })
  constructor(private router: Router, private dialogRef: MatDialogRef<NewBoardComponent>) { }

  ngOnInit(): void {
  }

  public createNewBoard() {
    if (this.validBoardName()) {
      this.dialogRef.close();
      this.router.navigateByUrl('board/1');
    }
  }

  public validBoardName(): boolean {
    return this.newBoardForm.valid;
  }
}
