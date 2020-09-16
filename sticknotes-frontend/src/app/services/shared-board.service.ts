import { Injectable } from '@angular/core';
import { Board, Note, BoardDescription, BoardGridLine } from '../interfaces';
import { BehaviorSubject, Observable } from 'rxjs';
import { BoardApiService } from './board-api.service';
import _ from 'lodash';

@Injectable({
  providedIn: 'root'
})
export class SharedBoardService {
  /**
   * Board object that will be shared across all services who need board data
   */
  private boardSubj = new BehaviorSubject<Board>(null);
  constructor(private boardApiService: BoardApiService) { }

  /**
   * Sets boardSubj value to null
   */
  public clear() {
    this.boardSubj.next(null);
  }

  public boardObservable(): Observable<Board> {
    return this.boardSubj.asObservable();
  }

  public loadBoard(boardId: string) {
    this.boardApiService.getBoard(boardId).subscribe(board => this.boardSubj.next(board));
  }

  /**
   * Adds new note to the board. This change is emitted to all subscribers
   */
  public newNote(note: Note) {
    this.boardSubj.value.notes.push(note);
    this.boardSubj.next(this.boardSubj.value);
  }

  /**
   * Updates local copy of given note. This change is emitted to all subscribers
   */
  public updateNote(note: Note) {
    this.boardSubj.value.notes.map(n => { 
      if (n.id === note.id) {
        n = _.merge(n, note);
      }
    });
    this.boardSubj.next(this.boardSubj.value);
  }

  /**
   * Deletes given note from shared board. This change is emitted to all subscribers
   */
  public deleteNote(note: Note) {
    const indexOfNote = this.boardSubj.value.notes.indexOf(note);
    if (indexOfNote >= 0 && indexOfNote < this.boardSubj.value.notes.length) {
      this.boardSubj.value.notes.splice(indexOfNote, 1);
    }
    this.boardSubj.next(this.boardSubj.value);
  }

  /**
   * Updates board subject
   */
  public updateBoard(board: BoardDescription) {
    let oldBoard = this.boardSubj.value;
    const newBoard = _.merge(oldBoard, board);
    this.boardSubj.next(newBoard);
  }

  /**
   * Adds the BoardGridLine to a list of board lines.
   * This change is emitted to all subscribers
   */
  public addGridLine(line: BoardGridLine) {
    this.boardSubj.value.gridLines.push(line);
    this.boardSubj.next(this.boardSubj.value);
  }
}
