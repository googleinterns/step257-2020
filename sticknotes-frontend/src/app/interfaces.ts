import { UserRole } from './enums/user-role.enum';

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

export interface CreateNoteData {
  content: string;
  image?: string;
  color: string;
  x: number;
  y: number;
  boardKey: string;
}

export interface Note extends CreateNoteData {
  key: string;
  creationDate: string;
  creator: string;
}
