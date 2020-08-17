import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { User, UserWithRole } from '../interfaces';
import { UserRole } from '../models/user-role.enum';

@Injectable({
  providedIn: 'root'
})
export class BoardUsersApiService {

  private users: UserWithRole[];

  constructor() { 
    this.users = [];
    this.users.push({
      user: {
        key: 'key0',
        nickname: 'admin',
        email: 'admin@google.com',
        accessibleBoards: []
      },
      role: UserRole.ADMIN
    })
    for (let i = 1; i < 10; i++) {
      this.users.push({
        user: {
          key: `key${i}`,
          nickname: `user${i}`,
          email: `user${i}@google.com`,
          accessibleBoards: []
        },
        role: UserRole.USER
      });
    }
  }

  public getBoardUsers(boardKey: string): Observable<UserWithRole[]> {
    return of(this.users);
  }

  public addBoardUser(userWithRole: UserWithRole): Observable<void> {
    this.users.push(userWithRole);
    return of();
  }

  public removeUser(userKey: string): Observable<void> {
    const indexOfUser = this.users.findIndex(user => user.user.key === userKey);
    this.users.splice(indexOfUser, 1);
    return of();
  }
}
