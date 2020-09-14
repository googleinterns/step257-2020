import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Board, BoardData, Note, BoardPreview, BoardUpdateData } from '../interfaces';

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
   * Craetes a board with the given title and returns the result
   */
  public createBoard(boardTitle: string): Observable<Board> {
    return this.http.post<Board>('api/board/', { title: boardTitle });
  }

  public updateBoard(data: BoardData): Observable<void> {
    const updateData: BoardUpdateData = {
      id: data.id,
      title: data.title,
      rows: data.rows,
      cols: data.cols,
      backgroundImg: data.backgroundImg
    };
    return this.http.post<void>('api/edit-board/', updateData);
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
}
