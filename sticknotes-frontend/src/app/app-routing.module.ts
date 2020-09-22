// Copyright 2020 Google LLC

import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginPageComponent } from './login-page/login-page.component';
import { BoardContainerComponent } from './board-container/board-container.component';
import { BoardsListComponent } from './boards-list/boards-list.component';
import { AuthGuard } from './guards/auth.guard';

const routes: Routes = [
  {
    path: 'board/:id',
    component: BoardContainerComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'boards',
    component: BoardsListComponent,
    canActivate: [AuthGuard]
  },
  {
    path: '',
    component: LoginPageComponent
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
