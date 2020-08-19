import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatDialogRef } from '@angular/material/dialog';
import { noSpacesValidator } from '../utility/util';

@Component({
  selector: 'app-new-board',
  templateUrl: './new-board.component.html',
  styleUrls: ['./new-board.component.css']
})
export class NewBoardComponent implements OnInit {
  public newBoardForm = new FormGroup({
    boardName: new FormControl('', [
      Validators.required,
      noSpacesValidator
    ])
  });
  constructor(private router: Router, private dialogRef: MatDialogRef<NewBoardComponent>) { }

  ngOnInit(): void {
  }

  public createNewBoard(): void {
    if (this.newBoardForm.valid) {
      this.dialogRef.close();
      this.router.navigateByUrl('board/1');
    }
  }
}
