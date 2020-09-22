// Copyright 2020 Google LLC

import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Vector2 } from '../utility/vector';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { noSpacesValidator } from '../utility/util';
import { CreateNoteApiData, Note, NotePopupData } from '../interfaces';
import { NotesApiService } from '../services/notes-api.service';
import { State } from '../enums/state.enum';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FileUploadService } from '../services/file-upload.service';
import { SharedBoardService } from '../services/shared-board.service';

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
  public editableNote: Note = null;
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
    ]),
    file: new FormControl({ value: '', disabled: this.editableNote && this.editableNote.image !== null }, [])
  });

  constructor(@Inject(MAT_DIALOG_DATA) private data: NotePopupData,
    private notesApiService: NotesApiService,
    private dialogRef: MatDialogRef<NewNoteComponent>,
    private snackBar: MatSnackBar,
    private fileUploadService: FileUploadService,
    private sharedBoard: SharedBoardService) {
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

  /**
   * Creates new note
   */
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
        boardId: this.boardId,
        image: null
      };
      const fileInput = this.newNoteForm.controls.file.value;
      // save image if it is there
      if (fileInput && fileInput.files.length) {
        const snackbarUpload = this.snackBar.open('Uploading image...');
        this.fileUploadService.uploadFile(fileInput.files[0]).subscribe(response => {
          // set attached image url to the payload
          noteData.image = response.fileUrl;
          snackbarUpload.dismiss();
          this.sendNoteCreateData(noteData);
        });
      } else {
        // otherwise just create without image
        this.sendNoteCreateData(noteData);
      }
    }
  }

  /**
   * Updates the note
   */
  private updateNote(): void {
    if (this.newNoteForm.valid) {
      // disable button
      this.sendingData = true;
      // update editableNote object fields
      this.editableNote.content = this.newNoteForm.controls.content.value;
      this.editableNote.color = this.getColorHexValue(this.newNoteForm.controls.options.value);
      const fileInput = this.newNoteForm.controls.file.value;
      // save image if it is there
      if (fileInput && fileInput.files.length) {
        const snackbarUpload = this.snackBar.open('Uploading image...');
        this.fileUploadService.uploadFile(fileInput.files[0]).subscribe(response => {
          // set attached image url to the payload
          this.editableNote.image = response.fileUrl;
          snackbarUpload.dismiss();
          this.sendNoteUpdateData(this.editableNote);
        });
      } else {
        // otherwise just update without image
        this.sendNoteUpdateData(this.editableNote);
      }
    }
  }

  /**
   * Sends create note payload
   */
  private sendNoteCreateData(payload: CreateNoteApiData) {
    // service returns a new note object
    this.notesApiService.createNote(payload).subscribe(note => {
      // successfully created, close the dialog and send new note to the shared board service
      this.sharedBoard.newNote(note);
      this.dialogRef.close();
    }, err => {
      // something went wrong
      this.snackBar.open("Server error occurred while creating a note", "Ok", {
        duration: 2000,
      });
      this.dialogRef.close();
    });
  }

  /**
   * Sends updated note to the server to update note's fields
   */
  private sendNoteUpdateData(payload: Note) {
    this.notesApiService.updateNote(payload).subscribe(note => {
      // successfully updated, close the dialog and send new note to the shared board service
      this.sharedBoard.updateNote(note);
      this.dialogRef.close();
    }, err => {
      // something went wrong
      this.snackBar.open("Server error occurred while updating a note", "Ok", {
        duration: 2000,
      });
      this.dialogRef.close();
    });
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

  /**
   * Removes image from editableNote.
   * Maybe need to send a request to cloud storage to delete a file indeed
   */
  public removeImage() {
    this.editableNote.image = null;
  }
}
