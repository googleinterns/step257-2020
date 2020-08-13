import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { BoardComponent } from './board/board.component';
import { BoardContainerComponent } from './board-container/board-container.component';
import { AccessibleBoardsComponent } from './accessible-boards/accessible-boards.component';

const routes: Routes = [
  {
    path: 'board-demo',
    component: BoardComponent
  },
  {
    path: 'board/:id',
    component: BoardContainerComponent
  },
  {
    path: 'accessibleBoards/:id',
    component: AccessibleBoardsComponent
  }
]

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
