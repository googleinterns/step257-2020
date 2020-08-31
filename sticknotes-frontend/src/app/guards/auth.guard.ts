import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { UserService } from '../services/user.service';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private userService: UserService, private router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
      // fetch user data firstly
      return this.userService.getUser().pipe(() => {
        // now when user data is loaded it can check for auth status
        // user data is not loaded each time getUser is accessed, only when local user variable is null
        return this.userService.isAuthenticated().pipe(tap(loggedIn => {
          // if user is not authenticated, redirect to home page
          if (!loggedIn) {
            this.router.navigateByUrl('/');
          }
        }));
      });
  }

}
