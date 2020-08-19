import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { Board, User } from '../interfaces';

@Injectable({
  providedIn: 'root'
})
export class BoardApiService {

  constructor(private http: HttpClient) { }

  public createBoard(boardTitle: string): Observable<Board> {
    // return this.http.post<Board>('api/boards/', {title: boardTitle});
    const mockedUser: User = {
      key: 'userKey',
      nickname: 'googler',
      email: 'googler@google.com',
      accessibleBoards: []
    };
    const mockedNewBoardResponse: Board = {
      key: 'boardKey',
      notes: [],
      users: [],
      creationDate: new Date().toISOString(),
      title: boardTitle,
      creator: mockedUser,
      rows: 4,
      cols: 6,
      backgroundImg: null
    }
    return of(mockedNewBoardResponse);
  }
}
