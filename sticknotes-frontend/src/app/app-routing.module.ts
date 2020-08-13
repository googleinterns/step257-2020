import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { LoginPageComponent } from './login-page/login-page.component'
import { AccessibleBoardsComponent } from './accessible-boards/accessible-boards.component';

const routes: Routes = [
  { path: 'login-page', component: LoginPageComponent },
  { path:'accessibleBoards/:id', component:AccessibleBoardsComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
