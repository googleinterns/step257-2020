import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Vector2 } from '../utility/vector';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { onlySpacesValidator } from '../utility/util';
import { CreateNoteApiData, Note, NotePopupData } from '../interfaces';
import { NotesApiService } from '../services/notes-api.service';
import { State } from '../enums/state.enum';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-new-note',
  templateUrl: './new-note.component.html',
  styleUrls: ['./new-note.component.css']
})
export class NewNoteComponent {
  // Position of the board in the grid, used in "create" mode
  private position: Vector2;
  // id of the board for which note is created
  private boardId: string;
  // This component can be in 2 states: editing the existing note or creating a new one
  private mode: State;
  // This object stores the note the user edits in the moment
  private editableNote: Note = null;
  // Text displayed in the "submit" button
  public submitButtonText: string;
  // To disable button when the data is being sent to the server
  public sendingData = false;

  // Form used in .html file, has a note content field and colors field
  public newNoteForm = new FormGroup({
    content: new FormControl('', [
      // note must not be empty
      Validators.required,
      // must not be of spaces only
      onlySpacesValidator,
      // must not have content longer than 256 characters
      Validators.maxLength(256) // just random number
    ]),
    // used in a radio buttons group
    // '1' means to select the first radio button as default
    colors: new FormControl('1', [
      // must not be empty
      Validators.required
    ])
  });

  /**
   * Depending on the state, parses the data passed from the caller component in the appropriate way and
   * initializes the component's fields
   */
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
      this.newNoteForm.controls.colors.setValue(this.getValueByHex(this.editableNote.color));
      this.submitButtonText = 'Update';
      this.mode = State.EDIT;
    }
  }

  /**
   * Creates a new note - validates the form and sends the POST request to the server
   * Passes the created note back to the caller component 
   */
  private createNote(): void {
    if (this.newNoteForm.valid) {
      // disable button
      this.sendingData = true;
      // construct note payload
      const noteContent = this.newNoteForm.controls.content.value;
      const noteColor = this.getColorHexValue(this.newNoteForm.controls.colors.value);
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

  /**
   * Updates an existing note - checks that the form is valid and sends data to the server
   * Passes the updated note back to the caller component 
   */
  private updateNote(): void {
    if (this.newNoteForm.valid) {
      // disable button
      this.sendingData = true;
      // update editableNote object fields
      this.editableNote.content = this.newNoteForm.controls.content.value;
      this.editableNote.color = this.getColorHexValue(this.newNoteForm.controls.colors.value);
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

  /**
   * Handles "submit" button click. Depending on the current state of the component, either creates or updates the note
   */
  public handleSubmit(): void {
    if (this.mode === State.EDIT) {
      this.updateNote();
    } else {
      this.createNote();
    }
  }

  /**
   * Converts radio button value to color hex value
   */
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

  /**
   * Converts color hex value to radio button value
   */
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
