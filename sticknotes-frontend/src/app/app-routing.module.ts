import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { BoardContainerComponent } from './board-container/board-container.component';

const routes: Routes = [
  {
    path: 'board/:id',
    component: BoardContainerComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
