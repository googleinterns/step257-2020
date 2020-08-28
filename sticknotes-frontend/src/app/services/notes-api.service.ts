import { Injectable } from '@angular/core';
import { CreateNoteApiData, Note } from '../interfaces';
import { Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class NotesApiService {

  constructor(private http: HttpClient) { }

  /**
   * Cretes a Note on the server from the data provided
   * Returns a created Note
   */
  public createNote(note: CreateNoteApiData): Observable<Note> {
    return this.http.post<Note>('api/notes/', note);
  }

  /**
   * Updates the note passed as the parameter. The note with the ID set in the
   * passed object gets all other fields of the passed object.
   * Returns updated note
   */
  public updateNote(note: Note): Observable<Note> {
    return this.http.post<Note>('api/edit-note/', note);
  }

  /**
   * Deletes the note with the given ID
   */
  public deleteNote(noteId: string): Observable<void> {
    return this.http.delete<void>(`api/notes/?id=${noteId}`);
  }
}
