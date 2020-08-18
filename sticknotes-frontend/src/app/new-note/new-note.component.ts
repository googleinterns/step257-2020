import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Vector2 } from '../utility/vector';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { noSpacesValidator } from '../utility/util';
import { CreateNoteApiData, Note, CreateNotePopupData } from '../interfaces';
import { NotesApiService } from '../services/notes-api.service';

@Component({
  selector: 'app-new-note',
  templateUrl: './new-note.component.html',
  styleUrls: ['./new-note.component.css']
})
export class NewNoteComponent implements OnInit {
  private position: Vector2;
  private boardKey: string;
  // this component can be in 2 states: editing the existing note or creating a new one
  public mode: 'edit' | 'create';
  // this object stores the note the user edits in the moment
  private editableNote: Note = null;
  public submitButtonText;
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
  });

  constructor(@Inject(MAT_DIALOG_DATA) private data: Note | CreateNotePopupData,
              private notesApiService: NotesApiService,
              private dialogRef: MatDialogRef<NewNoteComponent>) {
    if ('position' in data) {
      // creating new note
      const noteData = data as CreateNotePopupData;
      this.position = noteData.position;
      this.boardKey = noteData.boardKey;
      this.mode = 'create';
      this.submitButtonText = 'Create';
    }
    else {
      // editing existing note
      this.editableNote = data as Note;
      this.newNoteForm.controls.content.setValue(this.editableNote.content);
      this.newNoteForm.controls.options.setValue(this.getValueByHex(this.editableNote.color));
      this.submitButtonText = 'Update';
      this.mode = 'edit';
    }
  }

  ngOnInit(): void {
  }

  // creates new note
  public createNote(): void {
    if (this.newNoteForm.valid) {
      // disable button
      this.sendingData = true;
      // construct note payload
      const noteContent = this.newNoteForm.controls.content.value;
      const noteColor = this.getColorHexValue(this.newNoteForm.controls.options.value);
      const noteData: CreateNoteApiData = {
        x: this.position.x,
        y: this.position.y,
        content: noteContent,
        color: noteColor,
        boardKey: this.boardKey
      };
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

  // updates the note
  public updateNote(): void {
    if (this.newNoteForm.valid) {
      // disable button
      this.sendingData = true;
      // update editableNote object fields
      this.editableNote.content = this.newNoteForm.controls.content.value;
      this.editableNote.color = this.getColorHexValue(this.newNoteForm.controls.options.value);
      this.notesApiService.updateNote(this.editableNote).subscribe(note => {
        this.dialogRef.close(note);
      }, err => {
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

  public getValueByHex(color: string): string {
    switch (color) {
      case '#ffff99': {
        return '1';
      }
      case '#ccfff5': {
        return '2';
      }
      case '#ffe6ff': {
        return '3';
      } default: {
        return '4';
      }
    }
  }
}
