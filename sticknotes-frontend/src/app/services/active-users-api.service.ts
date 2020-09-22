// Copyright 2020 Google LLC

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ActiveUsers } from '../interfaces';

@Injectable({
  providedIn: 'root'
})
export class ActiveUsersApiService {

  constructor(private http: HttpClient) { }

  /**
   * getActiveUsers
   */
  public getActiveUsers(boardId: string): Observable<ActiveUsers>{
    return this.http.get<ActiveUsers>(`api/active-users/?id=${boardId}`);
  }
}
