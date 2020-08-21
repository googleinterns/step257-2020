import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { User, UserBoardRole } from '../interfaces';
import { UserRole } from '../enums/user-role.enum';

@Injectable({
  providedIn: 'root'
})
export class BoardUsersApiService {
  private users: UserBoardRole[];

  constructor() {
    this.users = [];
    this.users.push({
      user: {
        key: 'key0',
        nickname: 'admin',
        email: 'admin@google.com',
        accessibleBoards: []
      },
      boardId: 'boardId',
      role: UserRole.ADMIN
    });
    for (let i = 1; i < 3; i++) {
      this.users.push({
        user: {
          key: `key${i}`,
          nickname: `user${i}`,
          email: `user${i}@google.com`,
          accessibleBoards: []
        },
        boardId: 'boardId',
        role: UserRole.USER
      });
    }
  }

  public getBoardUsers(boardId: string): Observable<UserBoardRole[]> {
    // return deep copy of this.users instead of reference
    return of(JSON.parse(JSON.stringify(this.users)));
  }

  public addBoardUser(userEmail: string, userRole: UserRole): Observable<UserBoardRole> {
    const user: UserBoardRole = {
      user: {
        key: userEmail,
        nickname: userEmail.slice(0, userEmail.indexOf('@')),
        email: userEmail,
        accessibleBoards: []
      },
      boardId: 'boardId',
      role: userRole
    };
    this.users.push(user);
    return of(user);
  }

  public removeUser(userKey: string): Observable<string> {
    const indexOfUser = this.users.findIndex(user => user.user.key === userKey);
    this.users.splice(indexOfUser, 1);
    return of(userKey);
  }
}
