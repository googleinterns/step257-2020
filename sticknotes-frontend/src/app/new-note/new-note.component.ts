import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Vector2 } from '../utility/vector';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { noSpacesValidator } from '../utility/util';
import { CreateNoteData } from '../interfaces';
import { NotesApiService } from '../services/notes-api.service';

@Component({
  selector: 'app-new-note',
  templateUrl: './new-note.component.html',
  styleUrls: ['./new-note.component.css']
})
export class NewNoteComponent implements OnInit {
  private position: Vector2;
  private boardKey: string;

  // to disable button when the data is being sent to the server
  public sendingData = false;

  public newNoteForm = new FormGroup({
    content: new FormControl('', [
      Validators.required,
      noSpacesValidator,
      Validators.maxLength(128) // just random number
    ]),
    options: new FormControl('1', [
      Validators.required
    ])
  })
  constructor(@Inject(MAT_DIALOG_DATA) private data: { position: Vector2, boardKey: string },
    private notesApiService: NotesApiService,
    private dialogRef: MatDialogRef<NewNoteComponent>) {
    // receive position and board id from board-component when opening the dialog
    this.position = data.position;
    this.boardKey = data.boardKey;
  }

  ngOnInit(): void {
  }

  // creates new note
  public createNote() {
    if (this.newNoteForm.valid) {
      // disable button
      this.sendingData = true;
      // construct note payload
      const noteContent = this.newNoteForm.controls.content.value;
      const noteColor = this.getColorHexValue(this.newNoteForm.controls.options.value);
      const noteData: CreateNoteData = {
        x: this.position.x,
        y: this.position.y,
        content: noteContent,
        color: noteColor,
        boardKey: this.boardKey
      }
      // service returns a new note object
      this.notesApiService.createNote(noteData).subscribe(note => {
        // successfully created, close the dialog and pass the note back to the board
        this.dialogRef.close(note);
      }, err => {
        // something went wrong
        alert('Error occurred');
        this.dialogRef.close();
      });
    }
  }

  public getColorHexValue(val: string): string {
    switch (val) {
      case '1': {
        return '#ffff99';
      }
      case '2': {
        return '#ccfff5';
      }
      case '3': {
        return '#ffe6ff';
      } default: {
        return '#e6e6ff';
      }
    }
  }
}
