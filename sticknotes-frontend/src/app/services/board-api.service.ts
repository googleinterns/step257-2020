import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Board, BoardData, Note, BoardPreview } from '../interfaces';

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

  public updateBoard(data: BoardData): Observable<void> {
    return this.http.post<void>('api/edit-board/', data);
  }

  /**
   * Executes a request to translate notes of the board with id = boardId to language with targetLanguageCode
   */
  public translateNotesOfBoard(boardId: string, targetLanguageCode: string): Observable<Note[]> {
    return this.http.get<Note[]>(`api/board/notes/?id=${boardId}&lc=${targetLanguageCode}`);
  }

  /**
   * Returns a list of previews of boards available to user
   */
  public myBoardsList(): Observable<BoardPreview[]> {
    return this.http.get<BoardPreview[]>('api/myboards/');
  }
}
