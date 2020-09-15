import { Component, OnInit, OnDestroy } from '@angular/core';
import { CdkDragEnd, CdkDragStart } from '@angular/cdk/drag-drop';
import { Vector2 } from '../utility/vector';
import { getTranslateValues } from '../utility/util';
import { Note, Board, UserBoardRole, User, GridDimensionName } from '../interfaces';
import { NewNoteComponent } from '../new-note/new-note.component';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { NotesApiService } from '../services/notes-api.service';
import { State } from '../enums/state.enum';
import { LiveUpdatesService } from '../services/live-updates.service';
import _ from 'lodash';
import { MatSnackBar } from '@angular/material/snack-bar';
import { BoardUsersApiService } from '../services/board-users-api.service';
import { UserService } from '../services/user.service';
import { UserRole } from '../enums/user-role.enum';
import { SharedBoardService } from '../services/shared-board.service';
import { BoardApiService } from '../services/board-api.service';

/**
 * Component for displaying grid and notes
 */
@Component({
  selector: 'app-board',
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.css']
})
export class BoardComponent implements OnInit, OnDestroy {

  private boardGrid: number[][];
  public board: Board;
  public readonly NOTE_WIDTH = 200;
  public readonly NOTE_HEIGHT = 250;
  public readonly MARGIN_BETWEEN_ADJ_NOTES = 6; // margin between adjacent notes is 6px
  public readonly COLUMN_NAME_PADDING = 6; // padding of the column name element
  private boardRoles: UserBoardRole[] = [];
  private currentUserRole: UserRole = null;
  private currentUser: User = null;
  public boardColumnNames: GridDimensionName[] = null;

  constructor(private dialog: MatDialog,
    private activatedRoute: ActivatedRoute,
    private notesApiService: NotesApiService,
    private sharedBoard: SharedBoardService,
    private boardUsersApiService: BoardUsersApiService,
    private liveUpdatesService: LiveUpdatesService,
    private userService: UserService,
    private boardApiService: BoardApiService) {
  }

  /**
   * Stop live updates
   */
  ngOnDestroy(): void {
    this.liveUpdatesService.unregisterBoard();
  }

  ngOnInit(): void {
    // load board
    this.activatedRoute.paramMap.subscribe(params => {
      const boardId = params.get('id'); // get board id from route param
      // load board with the key
      this.sharedBoard.loadBoard(boardId);
      // 
      this.boardApiService.getBoardColumns().subscribe(data => this.boardColumnNames = data);
      // subscribe to changes of the board
      this.sharedBoard.boardObservable().subscribe(board => {
        if (board) {
          this.board = board;
          this.updateBoardAbstractGrid();
          // setup live updates
          if (!this.liveUpdatesService.hasRegisteredBoard()) {
            this.liveUpdatesService.registerBoard(board);
          }
        }
      });
    });

    // fetch board roles, first emitted value is default, second is actual array
    this.boardUsersApiService.getBoardUsersSubject().subscribe(roles => {
      this.boardRoles = roles;
    });

    // fetch current user
    this.userService.getUser().subscribe(user => {
      this.currentUser = user;
    });
  }

  /**
   * Updates the z-index of the note
   */
  public onNoteDragStart(cdkDragStart: CdkDragStart): void {
    const elementRef = cdkDragStart.source.element.nativeElement;
    elementRef.style.setProperty('z-index', '10');
  }

  /**
   * Moves a note to a proper position after it was released, resets z-index
   */
  public onNoteDrop(cdkDragEnd: CdkDragEnd, note: Note): void {
    const elementRef = cdkDragEnd.source.element.nativeElement;
    // reset z-index
    elementRef.style.setProperty('z-index', '3');
    const curTranslate = getTranslateValues(elementRef);
    // free currently taken note position
    this.boardGrid[note.y][note.x] = 0;
    // get closest free point
    const closestPoint = this.getClosestFreeSlot(note, (note.x * this.NOTE_WIDTH) + curTranslate.x, (note.y * this.NOTE_HEIGHT) + curTranslate.y);
    // set the new position of the note on the board
    this.boardGrid[closestPoint.y][closestPoint.x] = 1;
    // apply new transformation
    note.x = closestPoint.x;
    note.y = closestPoint.y;
    cdkDragEnd.source._dragRef.reset();
    elementRef.style.transform = '';
    // update note data
    this.notesApiService.updateNote(note).subscribe();
  }

  /**
   * Updates boardGrid with the positions of notes
   */
  public updateBoardAbstractGrid(): void {
    this.boardGrid = [];
    for (let i = 0; i < this.board.rows; ++i) {
      this.boardGrid[i] = [];
      for (let j = 0; j < this.board.cols; ++j) {
        this.boardGrid[i][j] = 0;
      }
    }
    if (this.board.notes) {
      this.board.notes.forEach(note => {
        const i = note.y;
        const j = note.x;
        this.boardGrid[i][j] = 1;
      });
    }
  }

  /**
   * Returns the closes available position to the given x and y
   */
  public getClosestFreeSlot(note: Note, x: number, y: number): Vector2 {
    // get the closest cells indices
    const closePoints = [];
    closePoints.push(new Vector2(Math.floor(x / this.NOTE_WIDTH) * this.NOTE_WIDTH, Math.floor(y / this.NOTE_HEIGHT) * this.NOTE_HEIGHT));
    closePoints.push(new Vector2((Math.floor(x / this.NOTE_WIDTH) + 1) * this.NOTE_WIDTH, Math.floor(y / this.NOTE_HEIGHT) * this.NOTE_HEIGHT));
    closePoints.push(new Vector2((Math.floor(x / this.NOTE_WIDTH) + 1) * this.NOTE_WIDTH, (Math.floor(y / this.NOTE_HEIGHT) + 1) * this.NOTE_HEIGHT));
    closePoints.push(new Vector2(Math.floor(x / this.NOTE_WIDTH) * this.NOTE_WIDTH, (Math.floor(y / this.NOTE_HEIGHT) + 1) * this.NOTE_HEIGHT));
    let closestPoint = closePoints[0];
    const curentPosition = new Vector2(x, y);
    // find the closest one
    for (const p of closePoints) {
      if (p.distanceTo(curentPosition) < closestPoint.distanceTo(curentPosition)) {
        closestPoint = p;
      }
    }
    // now start BFS from this point to find closest free cell
    const queue = [];
    const used = new Set<Vector2>();
    const adjacents = [];
    // direct border
    adjacents.push(new Vector2(-1, 0));
    adjacents.push(new Vector2(0, -1));
    adjacents.push(new Vector2(0, 1));
    adjacents.push(new Vector2(1, 0));
    // corner border
    adjacents.push(new Vector2(-1, 1));
    adjacents.push(new Vector2(-1, -1));
    adjacents.push(new Vector2(1, 1));
    adjacents.push(new Vector2(1, -1));

    queue.push(new Vector2(closestPoint.x / this.NOTE_WIDTH, closestPoint.y / this.NOTE_HEIGHT));
    while (queue.length) {
      const v = queue.shift();
      // check if point is free, return it
      if (this.boardGrid[v.y][v.x] === 0) {
        return v;
      }
      used.add(v);
      for (const adj of adjacents) {
        const p = adj.add(v);
        if (p.x >= 0 && p.x < this.board.cols && p.y >= 0 && p.y < this.board.rows && this.boardGrid[p.y][p.x] === 0 && !used.has(p)) {
          queue.push(p);
        }
      }
    }
  }

  /**
   * Generates a correct style to position the note
   */
  public getNoteStyle(note: Note): string {
    return `left:${note.x * this.NOTE_WIDTH}px;top:${note.y * this.NOTE_HEIGHT}px`;
  }

  /** 
   * Generates a correct style to position the slot
   */ 
  public getSlotStyle(x: number, y: number): string {
    return `left:${x * this.NOTE_WIDTH}px;top:${y * this.NOTE_HEIGHT}px`;
  }

  /**
   * Opens new-note component in a dialog and passes the position where the note has to be created.
   * Dialog sends new note to the board data service
   */
  public openNewNoteDialog(x: number, y: number): void {
    this.dialog.open(NewNoteComponent, {
      data: { mode: State.CREATE, noteData: { position: new Vector2(x, y), boardId: this.board.id } }
    });
  }

  /**
   * Opened dialog sends updated data to the board data service
   */
  public openEditNoteDialog(note: Note): void {
    this.dialog.open(NewNoteComponent, {
      data: { mode: State.EDIT, noteData: note }
    });
  }

  public deleteNote(note: Note): void {
    const reallyWantToDelete = confirm('Delete this note?');
    if (reallyWantToDelete) {
      this.notesApiService.deleteNote(note.id).subscribe(() => {
        // set 0 to the position of the note
        this.boardGrid[note.y][note.x] = 0;
        // update shared board
        this.sharedBoard.deleteNote(note);
      });
    }
  }

  public getBoardWidth() {
    return `width:${this.NOTE_WIDTH * this.board.cols}px;`;
  }

  public getBoardHeight() {
    return `height:${this.NOTE_HEIGHT * this.board.rows}px;`;
  }

  public getBoardStyle() {
    return `${this.getBoardWidth()} ${this.getBoardHeight()}`;
  }

  public getBoardWrapperWidth() {
    return `width: min(100% - 100px, ${this.NOTE_WIDTH * this.board.cols}px);`;
  }

  public getBoardWrapperHeight() {
    return `height: min(100% - 70px, ${this.NOTE_HEIGHT * this.board.rows}px);`;
  }

  public getRCWrapperWidth() {
    return `width: min(100%, ${(this.NOTE_WIDTH * this.board.cols) + 80}px);`;
  }

  public getRCWrapperHeight() {
    return `height: min(100% - 40px, ${this.NOTE_HEIGHT * this.board.rows}px);`;
  }

  public getRCWrapperStyle() {
    return `${this.getRCWrapperWidth()} ${this.getRCWrapperHeight()}`
  }

  public getColumnNameDivStyle(el: GridDimensionName) {
    // the width of the columns header is the width of columns - left and right margin, which is equal to margin between adjacent notes
    return `width: ${(Math.abs(el.rangeEnd - el.rangeStart + 1) * this.NOTE_WIDTH) - this.MARGIN_BETWEEN_ADJ_NOTES - this.COLUMN_NAME_PADDING}px;`;
  }

  public getBoardWrapperStyle() {
    // if board is wider than 100% of the screen or higher than 100%, set fixed width and height
    return `${this.getBoardWrapperWidth()} ${this.getBoardWrapperHeight()}`; 
  }

  public getNoteCreationDate(note: Note) {
    return new Date(Number(note.creationDate));
  }

  /**
   * Returns true if user can modify note.
   * Returns false otherwise.
   */
  public canModifyNote(note: Note) {
    if (this.currentUser && this.boardRoles) {
      if (!this.currentUserRole) {
        // save user's role if it is not saved yet
        this.currentUserRole = this.boardRoles.find(role => role.user.id === this.currentUser.id).role;
      }
      // if user is owner or admin, return true
      if (this.currentUserRole === 'ADMIN' || this.currentUserRole === 'OWNER') {
        return true;
      }
      // if user is author of the note also return true
      return this.currentUser.id === note.creator.id;
    }
    return false;
  }
}
