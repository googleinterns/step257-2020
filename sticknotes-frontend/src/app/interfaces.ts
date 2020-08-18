import { UserRole } from "./models/user-role.enum";

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

export interface Note {
  key: string;
  content: string;
  image: string | null;
  creationDate: string;
  color: string;
  creator: string;
  x: number;
  y: number;
}
