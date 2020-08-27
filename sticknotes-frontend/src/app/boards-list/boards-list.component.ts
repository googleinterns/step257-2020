import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { NewBoardComponent } from '../new-board/new-board.component';
import { BoardApiService } from '../services/board-api.service';
import { BoardPreview } from '../interfaces';

@Component({
  selector: 'app-boards-list',
  templateUrl: './boards-list.component.html',
  styleUrls: ['./boards-list.component.css']
})
export class BoardsListComponent implements OnInit {
  public myBoards: BoardPreview[] = null;

  constructor(private dialog: MatDialog, private boardApiService: BoardApiService) { }

  ngOnInit(): void {
    this.boardApiService.myBoardsList().subscribe(boards => {
      this.myBoards = boards;
    });
  }

  public showNewBoardDialog(): void {
    this.dialog.open(NewBoardComponent, {
      width: '500px',
    });
  }

  /**
   * Creates a link to the board
   */
  public getBoardLink(boardPreview: BoardPreview) {
    return `boards/${boardPreview.id}`;
  }
}
