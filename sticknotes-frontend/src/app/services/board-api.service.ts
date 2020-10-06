// Copyright 2020 Google LLC

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { Board, BoardDescription, BoardGridLine, BoardPreview, BoardUpdateData } from '../interfaces';

@Injectable({
  providedIn: 'root'
})
export class BoardApiService {

  constructor(private http: HttpClient) { }

  /**
   * Fetches the board
   */
  public getBoard(boardId: string): Observable<Board> {
    return this.http.get<Board>(`api/board/?id=${boardId}`);
  }

  /**
   * Create a new board
   */
  public createBoard(boardTitle: string): Observable<Board> {
    return this.http.post<Board>('api/board/', { title: boardTitle });
  }

  /**
   * Updates board's mutable fields
   */
  public updateBoard(data: BoardDescription): Observable<Board> {
    const updateData: BoardUpdateData = {
      id: data.id,
      title: data.title,
      rows: data.rows,
      cols: data.cols,
      backgroundImg: data.backgroundImg
    };
    return this.http.post<Board>('api/edit-board/', updateData);
  }

  /**
   * Returns a list of previews of boards available to user
   */
  public myBoardsList(): Observable<BoardPreview[]> {
    return this.http.get<BoardPreview[]>('api/myboards/');
  }

  /**
   * Returns the updated board if there is any update
   */
  public getUpdatedBoard(data: any): Observable<Board> {
    return this.http.post<Board>('api/board-updates/', data);
  }

  /**
   * Deletes the board
   */
  public deleteBoard(boardId: string): Observable<void> {
    return this.http.delete<void>(`api/board/?id=${boardId}`);
  }

  /**
   * Creates a BoardGridLine (column/row name)
   */
  public createBoardGridLine(line: any, boardId: string): Observable<BoardGridLine> {
    const payload = {...line};
    payload.boardId = boardId;
    return this.http.post<BoardGridLine>('api/board-grid-lines/', payload);
  }

  /**
   * Deletes a BoardGridLine (column/row name)
   */
  public deleteBoardGridLine(line: BoardGridLine): Observable<any> {
    return this.http.delete(`api/board-grid-lines/?id=${line.id}`);
  }

  /**
   * Edits a BoardGridLin (column/row name)
   */
  public editBoardGridLine(line: BoardGridLine): Observable<BoardGridLine> {
    return this.http.post<BoardGridLine>('api/edit-board-grid-lines/', line);
  }
}
