import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Board, Note } from '../interfaces';

@Injectable({
  providedIn: 'root'
})
export class BoardApiService {

  constructor(private http: HttpClient) { }

  public getBoard(boardId: string): Observable<Board> {
    return this.http.get<Board>(`api/board/?id=${boardId}`);
  }

  public createBoard(boardTitle: string): Observable<Board> {
    return this.http.post<Board>('api/board/', { title: boardTitle });
  }

  public updateBoardTitle(boardId: string, boardTitle: string): Observable<Board> {
    const payload = {
      id: boardId,
      title: boardTitle
    };
    return this.http.post<Board>('api/edit-board/', payload);
  }

  public translateNotesOfBoard(boardId: string, destinationLanguage: string): Observable<Note[]> {
    return this.http.get<Note[]>(`api/board/notes/?id=${boardId}&lc=${destinationLanguage}`);
  }
}
