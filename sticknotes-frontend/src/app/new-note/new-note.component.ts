import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Vector2 } from '../utility/vector';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { noSpacesValidator } from '../utility/util';

@Component({
  selector: 'app-new-note',
  templateUrl: './new-note.component.html',
  styleUrls: ['./new-note.component.css']
})
export class NewNoteComponent implements OnInit {
  private position: Vector2;

  public newNoteForm = new FormGroup({
    content: new FormControl('', [
      Validators.required,
      noSpacesValidator,
      Validators.maxLength(128) // just random number
    ])
  })
  constructor(@Inject(MAT_DIALOG_DATA) private data: Vector2) {
    // receive position of the note passed from board-component when opening the dialog
    this.position = data;
  }

  ngOnInit(): void {
  }

}
