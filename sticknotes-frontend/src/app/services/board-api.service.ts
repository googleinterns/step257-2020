import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Board } from '../interfaces';

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
    return this.http.patch<Board>(`api/boards/?id=${boardId}`, { title: boardTitle });
  }
}
