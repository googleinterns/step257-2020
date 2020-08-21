import { Injectable } from '@angular/core';
import { CreateNoteApiData, Note } from '../interfaces';
import { Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class NotesApiService {

  constructor(private http: HttpClient) { }

  public createNote(note: CreateNoteApiData): Observable<Note> {
    return this.http.post<Note>('api/notes/', note);
  }

  public updateNote(note: Note): Observable<Note> {
    return this.http.post<Note>('api/edit-note/', note);
  }

  public deleteNote(noteId: string): Observable<void> {
    // return this.http.delete<void>(`api/notes?key=${noteId}`);
    return of(undefined);
  }
}
