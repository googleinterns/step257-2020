import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { NewBoardComponent } from '../new-board/new-board.component';
import { BoardApiService } from '../services/board-api.service';
import { UserService } from '../services/user.service';
import { BoardPreview, User } from '../interfaces';

@Component({
  selector: 'app-boards-list',
  templateUrl: './boards-list.component.html',
  styleUrls: ['./boards-list.component.css']
})
export class BoardsListComponent implements OnInit {
  public myBoards: BoardPreview[] = null;
  //temporary solution before we introduce guards

  constructor(private dialog: MatDialog, private boardApiService: BoardApiService, private userService: UserService) { }

  ngOnInit(): void {
    //ensuring right order of fetching data
    this.userService.getUser().subscribe(user => { 
      this.boardApiService.myBoardsList().subscribe(boards => {
        this.myBoards = boards;
      });
    });
  }

  public showNewBoardDialog(): void {
    this.dialog.open(NewBoardComponent);
  }

  /**
   * Creates a link to the board
   */
  public getBoardLink(boardPreview: BoardPreview) {
    return `/board/${boardPreview.id}`;
  }
}
