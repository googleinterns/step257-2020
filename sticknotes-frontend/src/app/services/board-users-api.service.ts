/**
 * Service used to manage and retrieve list of board users.
 */
import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs';
import { UserBoardRole } from '../interfaces';
import { UserRole } from '../enums/user-role.enum';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class BoardUsersApiService {
  // UserBoardRole array is used in many places, so to share data across components
  // subject is used
  private userBoardRolesSubject = new BehaviorSubject<UserBoardRole[]>(null);
  constructor(private http: HttpClient) {
  }

  //returns all users of the board and saves them in subject
  public getBoardUsers(boardId: string): Observable<UserBoardRole[]> {
    return this.http.get<UserBoardRole[]>(`api/board/users/?id=${boardId}`).pipe(tap(data => {
      this.userBoardRolesSubject.next(data);
    }));
  }

  //return users subject
  public getBoardUsersSubject(): Observable<UserBoardRole[]> {
    return this.userBoardRolesSubject;
  }

  //sends request to add user to board
  public addBoardUser(boardId: string, userEmail: string, userRole: UserRole): Observable<UserBoardRole> {
    return this.http.post<UserBoardRole>(`api/board/users/?id=${boardId}`, { email: userEmail, role: userRole });
  }

  //sends request to remove user from board
  public removeUser(boardId: string, userRoleKey: string): Observable<void> {
    return this.http.delete<void>(`api/board/users/?id=${userRoleKey}&board-id=${boardId}`);
  }

  //sends request to edit role of user on the board
  public editUserRole(boardId: string, userRoleKey: string, newRole: string): Observable<void> {
    return this.http.post<void>('api/edit-role/',{boardId: boardId, roleId: userRoleKey, newRole: newRole});
  }
}
