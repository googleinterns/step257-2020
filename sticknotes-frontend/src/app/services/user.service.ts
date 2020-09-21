/**
 * This service allows to:
 * - get authentication status
 * - get currently logged in user
 * - get login link
 * - get logout link
 */
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
  }

  private fetch(): Observable<User> {
    return this.http.get('api/user/').pipe(map((fetchedUser: User) => {
      this.authenticated.next(true);
      this.userSubject.next(fetchedUser);
      return this.userSubject.value;
    }));
  }

  /**
   * If user was not fetched yet function fetches the user, if fetching was successful 
   * returns true, else it returns false. If user is already available in the userSubject
   * function returns current value of au
   */
  isAuthenticated(): Observable<boolean> {
    if (this.userSubject.value == null) {
      return this.fetch().pipe(map((user: User) => {
        if (user) {
          return true;
        }
        return false;
      }));
    }
    return this.authenticated.asObservable();
  }
  /**
   * returns observable of user or fetches the user if user was not fetched yet
   */
  getUser(): Observable<User> {
    if (this.userSubject.value === null) {
      return this.fetch();
    }
    return this.userSubject.asObservable();
  }
  /**
   * removes user by pushing null value to the userSubject
   */
  removeUser(): void {
    this.userSubject.next(null);
  }
  /**
   * fetches login url from server
   */
  getLoginUrl(): Observable<{ url: string }> {
    return this.http.get<{ url: string }>('api/login-url/');
  }
  /**
   * fetches logout url from server
   */
  getLogoutUrl(): Observable<{url: string}> {
    return this.http.get<{url: string}>('api/logout-url/');
  }
}
