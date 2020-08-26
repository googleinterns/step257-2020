import { Component, OnInit, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DOCUMENT } from '@angular/common';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.css']
})
export class LoginPageComponent implements OnInit {

  public loginUrl = null;

  constructor(private http: HttpClient, @Inject(DOCUMENT) private document: Document) {
    this.http.get(`api/login-url/`, {responseType: 'text'}).subscribe((loginUrl: string) => {
      this.loginUrl = loginUrl;
    })
  }

  ngOnInit(): void {
  }

  goToUrl(): void {
    console.log(this.loginUrl);
    this.document.location.href = this.loginUrl;
}
}
