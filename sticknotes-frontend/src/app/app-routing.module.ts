import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginPageComponent } from './login-page/login-page.component'
import { BoardContainerComponent } from './board-container/board-container.component';
import { AccessibleBoardsComponent } from './accessible-boards/accessible-boards.component';

const routes: Routes = [
  {
    path: 'board/:id',
    component: BoardContainerComponent
  },
  {
    path: 'accessibleBoards',
    component: AccessibleBoardsComponent
  },
  { 
    path: 'login-page', 
    component: LoginPageComponent 
  }
]

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
