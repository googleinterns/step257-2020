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

  goToUrl(): void {
    window.location.href = this.loginUrl;
  }
}
