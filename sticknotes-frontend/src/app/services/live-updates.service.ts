import { Injectable } from '@angular/core';
import { BoardDataService } from './board-data.service';
import { Board, NoteUpdateRequest, Note } from '../interfaces';
import _ from 'lodash';
import { NotesApiService } from './notes-api.service';
import { BoardApiService } from './board-api.service';
import { forkJoin } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LiveUpdatesService {
  // board for which currently updates are fetched
  private board: Board = null;
  // reference to a function created by setInterval
  private intervalFunction: any = null;
  // interval between updates requests in milliseconds
  private readonly UPDATE_INTERVAL = 2000;
  constructor(private boardDataService: BoardDataService, private notesApiService: NotesApiService, private boardApiService: BoardApiService) { }

  /**
   * Sets a board for which updates must be fetched.
   * Starts interval function.
   */
  public registerBoard(board: Board) {
    this.board = board;
    // fetch updates data each 2 seconds from the server
    this.intervalFunction = setInterval(() => {
      this.fetchUpdate();
    }, this.UPDATE_INTERVAL);
  }

  /**
   * Stops requesting updates
   */
  public unregisterBoard() {
    clearInterval(this.intervalFunction);
    this.board = null;
  }

  /**
   * Fetches an updated content of notes and board from the server
   */
  private fetchUpdate() {
    if (this.board) {
      // generate notes update request
      const notesUpdatesRequestData: NoteUpdateRequest[] = [];
      this.board.notes.forEach(note => {
        notesUpdatesRequestData.push({ id: note.id, lastUpdated: this.getNoteLastUpdated(note) });
      });
      const boardUpdatesRequestData = { id: this.board.id, lastUpdated: this.getBoardLastUpdated(this.board) };
      // request notes and board updates
      forkJoin([this.notesApiService.getUpdatedNotes(notesUpdatesRequestData, this.board.id),
      this.boardApiService.getUpdatedBoard(boardUpdatesRequestData)]).subscribe(([notes, board]) => {
        const newNotes = notes.updatedNotes;
        const removedNotes = notes.removedNotes;
        // server returns array of notes that have been removed, this notes have to be removed also here
        removedNotes.forEach(id => {
          const index = this.board.notes.findIndex((note) => id === Number(note.id));
          if (index >= 0 && index < this.board.notes.length) {
            this.board.notes.splice(index, 1);
          }
        });
        // server returns array of notes that have been changed, find local copy of that notes and update them
        // insert notes that are new
        const ids = new Set(this.board.notes.map(n => n.id));
        newNotes.forEach(note => {
          // if note is new, add it to the board
          if (!ids.has(note.id)) {
            this.board.notes.push(note);
          } else {
            // otherwise if it was changed, change its local copy
            // find index of updated note
            const index = this.board.notes.findIndex((n) => n.id === note.id);
            if (index >= 0 && index < this.board.notes.length) {
              // if not with such id is found, update it
              this.board.notes[index] = _.merge(this.board.notes[index], note);
            }
          }
        });
        // update board fields
        this.board = _.merge(this.board, board);
        // emit updated board
        this.boardDataService.updateBoard(this.board);
      });
    }
  }

  /**
   * Helper function that returns when given note was last updated
   */
  private getNoteLastUpdated(note: Note): string {
    if (note.lastUpdated) {
      return note.lastUpdated;
    }
    return "0";
  }

  /**
   * Helper function that returns when given board was last updated
   */
  private getBoardLastUpdated(board: Board): string {
    if (board.lastUpdated) {
      return board.lastUpdated;
    }
    return "0";
  }

  /**
   * Returns true if service has a board for which it fetches updates, and false otherwise
   */
  public hasRegisteredBoard() {
    return this.board !== null;
  }
}
