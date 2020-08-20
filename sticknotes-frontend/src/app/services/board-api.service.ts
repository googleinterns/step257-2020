import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { Board, User, Note } from '../interfaces';

@Injectable({
  providedIn: 'root'
})
export class BoardApiService {
  private mockedUser: User = {
    key: 'userKey',
    nickname: 'googler',
    email: 'googler@google.com',
    accessibleBoards: []
  };
  private mockedNewBoardResponse: Board = {
    key: 'boardKey',
    notes: [],
    users: [],
    creationDate: this.currentTimestamp,
    title: 'boardTitle',
    creator: this.mockedUser,
    rows: 4,
    cols: 6,
    backgroundImg: null
  }

  constructor(private http: HttpClient) { }

  public getBoard(boardId: string): Observable<Board> {
    return this.http.get<Board>(`api/board/?id=${boardId}`);
  }

  public createBoard(boardTitle: string): Observable<Board> {
    return this.http.post<Board>('api/board/', {title: boardTitle});
  }

  public updateBoardTitle(boardId: string, boardTitle: string): Observable<Board> {
    return this.http.patch<Board>(`api/boards/?id=${boardId}`, {title: boardTitle});
  }
}
