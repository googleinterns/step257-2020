import { Injectable } from '@angular/core';
import { BehaviorSubject, of, Observable, ReplaySubject } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { User } from '../interfaces';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private authenticated: ReplaySubject<boolean> = new ReplaySubject(1);
  private userSubject: BehaviorSubject<User> = new BehaviorSubject(null);

  constructor(private http: HttpClient) {
    this.fetch();
  }

  private fetch(): Observable<User> {
    return this.http.get('api/user/').pipe(map((fetchedUser: User) => {
      this.authenticated.next(true);
      this.userSubject.next(fetchedUser);
      return this.userSubject.value;
    }));
  }

  isAuthenticated(): Observable<boolean> {
    return this.authenticated.asObservable();
  }

  getUser(): Observable<User> {
    if (this.userSubject.value === null) {
      return this.fetch();
    }
    return this.userSubject.asObservable();
  }

  removeUser(): void {
    this.userSubject.next(null);
  }

  getLoginUrl(): Observable<{url: string}> {
    return this.http.get<{url: string}>('api/login-url/');
  }

  getLogoutUrl(): Observable<string> {
    return this.http.get<string>('api/logout-url/');
  }
}
