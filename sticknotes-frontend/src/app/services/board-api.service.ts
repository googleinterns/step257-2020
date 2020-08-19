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

  public getBoard(boardKey: string): Observable<Board> {
    const notes: Note[] = [];
    notes.push({
      key: 'key1',
      content: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque porta odio ut magna finibus scelerisque vel malesuada mi.',
      image: null,
      creationDate: this.currentTimestamp,
      creator: 'user@google.com',
      color: '#ffff99',
      boardKey: 'boardKey',
      x: 200,
      y: 0,
    });
    notes.push({
      key: 'key2',
      content: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque porta odio ut magna finibus scelerisque vel malesuada mi.',
      image: null,
      creationDate: this.currentTimestamp,
      creator: 'user2@google.com',
      color: '#ccfff5',
      boardKey: 'boardKey',
      x: 400,
      y: 500,
    });
    notes.push({
      key: 'key3',
      content: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque porta odio ut magna finibus scelerisque vel malesuada mi.',
      image: null,
      creationDate: this.currentTimestamp,
      creator: 'user3@google.com',
      color: '#ffe6ff',
      boardKey: 'boardKey',
      x: 600,
      y: 0,
    });
    notes.push({
      key: 'key4',
      content: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque porta odio ut magna finibus scelerisque vel malesuada mi.',
      image: null,
      creationDate: this.currentTimestamp,
      creator: 'user3@google.com',
      color: '#e6e6ff',
      boardKey: 'boardKey',
      x: 600,
      y: 500,
    });

    const creator: User = {
      key: 'userKey',
      nickname: 'Googler',
      accessibleBoards: [],
      email: 'user@google.com'
    };

    const board: Board = {
      key: boardKey,
      notes: notes,
      title: 'Board title',
      rows: 5,
      cols: 8,
      creator: creator,
      users: [],
      backgroundImg: null,
      creationDate: this.currentTimestamp,
    };

    creator.accessibleBoards.push(board);
    return of(board);
  }

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

  get currentTimestamp(): string {
    return Math.round(new Date().getTime()).toString()
  }
}
