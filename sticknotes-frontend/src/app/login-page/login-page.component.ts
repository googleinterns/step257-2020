// Copyright 2020 Google LLC
/**
 * Welcome component and entry point to the application. It displays
 * login button and fetches login url from the server. After successful
 * login, or if user was already logged in user is redirected to the
 * boards view.
 */

import { Component, OnInit, Inject } from '@angular/core';
import { UserService } from '../services/user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.css']
})
export class LoginPageComponent implements OnInit {

  public loginUrl = null;

  constructor(private userService: UserService, private router: Router) {
    this.userService.getLoginUrl().subscribe(data => {
      this.loginUrl = data.url;
    }, err => {
      // probably user is already logged in, redirect to boards/
      this.router.navigateByUrl('/boards');
    })
  }

  ngOnInit(): void {
  }

  //navigates to the loginUrl
  goToUrl(): void {
    window.location.href = this.loginUrl;
  }
}
