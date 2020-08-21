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
    // comment this for now because we don't have backend yet
    // return this.http.post<Note>('api/notes/', note);
    // use mocked response for now
    const mockedNoteResponse: Note = {
      id: 'id',
      creationDate: this.currentTimestamp,
      creator: 'googler@google.com',
      x: note.x,
      y: note.y,
      content: note.content,
      color: note.color,
      boardId: note.boardId
    };
    return of(mockedNoteResponse);
  }

  public updateNote(note: Note): Observable<Note> {
    // comment this for now because we don't have backend yet
    // return this.http.post<Note>('api/notes/', note);
    return of (note);
  }

  public deleteNote(noteId: string): Observable<void> {
    // return this.http.delete<void>(`api/notes?key=${noteId}`);
    return of(undefined);
  }

  get currentTimestamp(): string {
    return Math.round(new Date().getTime()).toString()
  }
}
