import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { User, UserBoardRole } from '../interfaces';
import { UserRole } from '../enums/user-role.enum';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class BoardUsersApiService {

  constructor(private http: HttpClient) {
  }

  public getBoardUsers(boardId: string): Observable<UserBoardRole[]> {
    return this.http.get<UserBoardRole[]>(`api/board/users/?id=${boardId}`);
  }

  public addBoardUser(boardId: string, userEmail: string, userRole: UserRole): Observable<UserBoardRole> {
    return this.http.post<UserBoardRole>(`api/board/users/?id=${boardId}`, {email: userEmail, role: userRole});
  }

  public removeUser(userRoleKey: string): Observable<void> {
    return this.http.delete<void>(`api/board/users/?id=${userRoleKey}`);
  }
}
