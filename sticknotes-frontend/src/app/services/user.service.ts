import { Injectable } from '@angular/core';
import { BehaviorSubject, of, Observable } from 'rxjs';
import { User } from '../interfaces';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private authenticated: BehaviorSubject<boolean> = new BehaviorSubject(false);

  private userSubject: BehaviorSubject<User> = new BehaviorSubject(null);

  constructor() {
    const user: User = {
      key: 'key0',
      nickname: 'user0',
      email: 'user0@google.com',
      accessibleBoards: []
    };
    of(user).subscribe(fetchedUser => {
      this.authenticated.next(true);
      this.userSubject.next(fetchedUser)
    });
  }

  isAuthenticated(): Observable<boolean> {
    return this.authenticated.asObservable();
  }

  getUser(): Observable<User> {
    return this.userSubject.asObservable();
  }

  removeUser(): void {
    this.userSubject.next(null);
  }
}
