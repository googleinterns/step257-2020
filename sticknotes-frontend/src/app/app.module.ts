import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatCardModule } from '@angular/material/card';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MatListModule } from '@angular/material/list';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { LoginPageComponent } from './login-page/login-page.component';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatButtonModule } from '@angular/material/button';
import { ElevateOnHoverDirective } from './directives/elevate-on-hover.directive';
import { BoardContainerComponent } from './board-container/board-container.component';
import { UserListComponent } from './user-list/user-list.component';
import { MatIconModule } from '@angular/material/icon';
import { BoardComponent } from './board/board.component';
import { BoardsListComponent } from './boards-list/boards-list.component';

@NgModule({
  declarations: [
    AppComponent,
    BoardComponent,
    ElevateOnHoverDirective,
    LoginPageComponent,
    BoardContainerComponent,
    UserListComponent,
    BoardsListComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    DragDropModule,
    MatCardModule,
    MatListModule,
    MatIconModule,
    MatSidenavModule,
    MatButtonModule,
    MatGridListModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
