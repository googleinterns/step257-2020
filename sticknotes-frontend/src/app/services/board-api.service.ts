import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Board } from '../interfaces';

@Injectable({
  providedIn: 'root'
})
export class BoardApiService {

  constructor(private http: HttpClient) { }

  /**
   * Retrieves the board with the given ID from the server
   */
  public getBoard(boardId: string): Observable<Board> {
    return this.http.get<Board>(`api/board/?id=${boardId}`);
  }

  /**
   * Craetes a board with the given title and returns the result
   */
  public createBoard(boardTitle: string): Observable<Board> {
    return this.http.post<Board>('api/board/', { title: boardTitle });
  }

  /**
   * Sets new title to the board with the given id
   * Makes a POST request on board edit endpoint
   * Returns the updated board
   */
  public updateBoardTitle(boardId: string, boardTitle: string): Observable<Board> {
    const payload = {
      id: boardId,
      title: boardTitle
    };
    return this.http.post<Board>('api/edit-board/', payload);
  }
}
