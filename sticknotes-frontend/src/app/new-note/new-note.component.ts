import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Vector2 } from '../utility/vector';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { noSpacesValidator } from '../utility/util';
import { CreateNoteApiData, Note, NotePopupData } from '../interfaces';
import { NotesApiService } from '../services/notes-api.service';
import { State } from '../enums/state.enum';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-new-note',
  templateUrl: './new-note.component.html',
  styleUrls: ['./new-note.component.css']
})
export class NewNoteComponent implements OnInit {
  private position: Vector2;
  private boardId: string;
  // this component can be in 2 states: editing the existing note or creating a new one
  private mode: State;
  // this object stores the note the user edits in the moment
  private editableNote: Note = null;
  public submitButtonText: string;
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

  constructor(@Inject(MAT_DIALOG_DATA) private data: NotePopupData,
    private notesApiService: NotesApiService,
    private dialogRef: MatDialogRef<NewNoteComponent>,
    private snackBar: MatSnackBar) {
    if (data.mode === State.CREATE) {
      // creating new note
      const noteData = data.noteData as { position: Vector2, boardId: string };
      this.position = noteData.position;
      this.boardId = noteData.boardId;
      this.mode = State.CREATE;
      this.submitButtonText = 'Create';
    }
    else {
      // editing existing note
      this.editableNote = data.noteData as Note;
      this.newNoteForm.controls.content.setValue(this.editableNote.content);
      this.newNoteForm.controls.options.setValue(this.getValueByHex(this.editableNote.color));
      this.submitButtonText = 'Update';
      this.mode = State.EDIT;
    }
  }

  ngOnInit(): void {
  }

  // creates new note
  private createNote(): void {
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
        boardId: this.boardId
      };
      // service returns a new note object
      this.notesApiService.createNote(noteData).subscribe(note => {
        // successfully created, close the dialog and pass the note back to the board
        this.dialogRef.close(note);
      }, err => {
        // something went wrong
        this.snackBar.open("Server error occurred while creating a note", "Ok", {
          duration: 2000,
        });
        this.dialogRef.close();
      });
    }
  }

  // updates the note
  private updateNote(): void {
    if (this.newNoteForm.valid) {
      // disable button
      this.sendingData = true;
      // update editableNote object fields
      this.editableNote.content = this.newNoteForm.controls.content.value;
      this.editableNote.color = this.getColorHexValue(this.newNoteForm.controls.options.value);
      this.notesApiService.updateNote(this.editableNote).subscribe(note => {
        this.dialogRef.close(note);
      }, err => {
        this.snackBar.open("Server error occurred while updating a note", "Ok", {
          duration: 2000,
        });
        this.dialogRef.close();
      });
    }
  }

  public handleSubmit(): void {
    if (this.mode === State.EDIT) {
      this.updateNote();
    } else {
      this.createNote();
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
