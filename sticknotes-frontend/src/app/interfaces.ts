export interface User {
  key: string;
  nickname: string;
  email: string;
  accessibleBoards: Board[];
}

export interface UserBoardRole {
  user: User;
  board: Board;
  role: string;
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

export interface SidenavBoardData {
  key: string;
  creationDate: string;
  title: string;
  backgroundImg: string | null;
}
