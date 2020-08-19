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
      key: 'key',
      creationDate: new Date().toISOString(),
      creator: 'googler@google.com',
      x: note.x,
      y: note.y,
      content: note.content,
      color: note.color,
      boardKey: note.boardKey
    };
    return of(mockedNoteResponse);
  }

  public updateNote(note: Note): Observable<Note> {
    // comment this for now because we don't have backend yet
    // return this.http.post<Note>('api/notes/', note);
    return of (note);
  }
}
