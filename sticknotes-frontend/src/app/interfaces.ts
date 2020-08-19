import { Vector2 } from './utility/vector';
import { State } from './enums/state.enum';
import { UserRole } from './models/user-role.enum';

export interface User {
  key: string;
  nickname: string;
  email: string;
  accessibleBoards: Board[];
}

export interface UserBoardRole {
  user: User;
  boardKey: string;
  role: UserRole;
}

export interface Board {
  key: string;
  notes: Note[];
  users: UserBoardRole[];
  creationDate: string;
  title: string;
  creator: User;
  rows: number;
  cols: number;
  backgroundImg: string | null;
}

export interface NotePopupData {
  mode: State;
  noteData: {position: Vector2, boardKey: string} | Note;
}

export interface CreateNoteApiData {
  content: string;
  image?: string;
  color: string;
  x: number;
  y: number;
  boardKey: string;
}

export interface Note extends CreateNoteApiData {
  key: string;
  creationDate: string;
  creator: string;
}

export interface SidenavBoardData {
  key: string;
  creationDate: string;
  title: string;
  backgroundImg: string | null;
}
