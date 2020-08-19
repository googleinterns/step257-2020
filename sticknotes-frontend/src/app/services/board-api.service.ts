import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { Board, User } from '../interfaces';

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
    creationDate: new Date(),
    title: 'boardTitle',
    creator: this.mockedUser,
    rows: 4,
    cols: 6,
    backgroundImg: null
  }

  constructor(private http: HttpClient) { }

  public createBoard(boardTitle: string): Observable<Board> {
    // return this.http.post<Board>('api/boards/', {title: boardTitle});
    return of(this.mockedNewBoardResponse);
  }

  public updateBoardTitle(boardKey: string, boardTitle: string): Observable<Board> {
    // return this.http.post<Board>(`api/boards/?id=${boardKey}`, {title: boardTitle});
    this.mockedNewBoardResponse.title = boardTitle;
    const mockedResponse = this.mockedNewBoardResponse;
    mockedResponse.title = boardTitle;
    return of(mockedResponse);
  }
}
