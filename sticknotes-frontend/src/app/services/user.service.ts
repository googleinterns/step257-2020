import { Injectable } from '@angular/core';
import { BehaviorSubject, of, Observable } from 'rxjs';
import { User } from '../interfaces';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  
  private userSubject: BehaviorSubject<User>;

  constructor() {
    const user: User = { 
      key: 'key0',
      nickname: 'user0',
      email: 'user0@google.com',
      accessibleBoards: []
    }
    this.userSubject = new BehaviorSubject(null);

    of(user).subscribe(user => this.userSubject.next(user));
  }
 
  getUser(): Observable<User> {
    return this.userSubject.asObservable();
  }

  removeUser(): void {
    this.userSubject.next(null);
  }
}
