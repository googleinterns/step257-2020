import { Vector2 } from './utility/vector';
import { State } from './enums/state.enum';
import { UserRole } from './enums/user-role.enum';

export interface User {
  id: string;
  nickname: string;
  email: string;
  accessibleBoards: Board[];
}

export interface UserBoardRole {
  id: string;
  user: User;
  boardId: string;
  role: UserRole;
}

export interface Board {
  id: string;
  notes: Note[];
  users: UserBoardRole[];
  creationDate: string;
  title: string;
  creator: User;
  rows: number;
  cols: number;
  backgroundImg: string | null;
  lastUpdated: string;
}

export interface NotePopupData {
  mode: State;
  noteData: { position: Vector2, boardId: string } | Note;
}

export interface CreateNoteApiData {
  content: string;
  image: string | null; // it is possible that note doesn't have image
  color: string;
  x: number;
  y: number;
  boardId: string;
}

export interface Note extends CreateNoteApiData {
  id: string;
  creationDate: string;
  creator: User;
  lastUpdated: string;
}

/**
 * Used to store updated data of the board when user edits the board and to display the board
 * data in the sidenav
 */
export interface BoardData {
  readonly id: string;
  readonly creationDate: string;
  readonly creator: User;
  title: string;
  rows: number;
  cols: number;
  backgroundImg: string | null;
}

/**
 * A set of mutable board fields
 */
export interface BoardUpdateData {
  readonly id: string;
  title: string;
  rows: number;
  cols: number;
  backgroundImg: string | null;
}

export interface BoardPreview {
  readonly id: string;
  readonly title: string;
}

export interface NoteUpdateRequest {
  id: string;
  lastUpdated: string;
}
