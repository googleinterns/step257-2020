// Copyright 2020 Google LLC

import { Injectable } from '@angular/core';
import { CreateNoteApiData, Note, NoteUpdateRequest, NotesUpdatesResponseData } from '../interfaces';
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
    const updateNoteData = {
      x: note.x,
      y: note.y,
      color: note.color,
      content: note.content,
      image: note.image,
      id: note.id
    };
    return this.http.post<Note>('api/edit-note/', updateNoteData);
  }

  public deleteNote(noteId: string): Observable<void> {
    return this.http.delete<void>(`api/notes/?id=${noteId}`);
  }

  /**
   * Returns updated notes
   */
  public getUpdatedNotes(data: NoteUpdateRequest[], boardId: string): Observable<NotesUpdatesResponseData> {
    const payload = {
      notes: data,
      boardId: boardId
    };
    return this.http.post<NotesUpdatesResponseData>('api/notes-updates/', payload);
  }
}
