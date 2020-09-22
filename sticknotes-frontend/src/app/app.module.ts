// Copyright 2020 Google LLC

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
import { NewBoardComponent } from './new-board/new-board.component';
import { ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatDialogModule } from '@angular/material/dialog';
import { UserListComponent } from './user-list/user-list.component';
import { MatIconModule } from '@angular/material/icon';
import { BoardComponent } from './board/board.component';
import { BoardsListComponent } from './boards-list/boards-list.component';
import { NewNoteComponent } from './new-note/new-note.component';
import { RangeLoopPipe, NoteContentPipe } from './pipes';
import { MatRadioModule } from '@angular/material/radio';
import { HttpClientModule } from '@angular/common/http';
import { MomentModule } from 'ngx-moment';
import { AddUserComponent } from './add-user/add-user.component';
import { BoardEditComponent } from './board-edit/board-edit.component';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSelectModule } from '@angular/material/select';
import { MaterialFileInputModule } from 'ngx-material-file-input';
import { EditUserComponent } from './edit-user/edit-user.component';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatMenuModule } from '@angular/material/menu';
import { MatTabsModule } from '@angular/material/tabs';
import { NewGridLineComponent } from './new-grid-line/new-grid-line.component';

@NgModule({
  declarations: [
    AppComponent,
    BoardComponent,
    ElevateOnHoverDirective,
    LoginPageComponent,
    BoardContainerComponent,
    NewBoardComponent,
    UserListComponent,
    BoardsListComponent,
    NewNoteComponent,
    RangeLoopPipe,
    NoteContentPipe,
    AddUserComponent,
    BoardEditComponent,
    EditUserComponent,
    NewGridLineComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    MatSidenavModule,
    MatIconModule,
    BrowserAnimationsModule,
    DragDropModule,
    MatCardModule,
    MatListModule,
    MatButtonModule,
    ReactiveFormsModule,
    MatInputModule,
    MatDialogModule,
    MatGridListModule,
    MatRadioModule,
    HttpClientModule,
    MomentModule,
    MatSnackBarModule,
    MatSelectModule,
    MaterialFileInputModule,
    MatTooltipModule,
    MatMenuModule,
    MatTabsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
