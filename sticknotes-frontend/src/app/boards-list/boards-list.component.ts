import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { NewBoardComponent } from '../new-board/new-board.component';
import { BoardApiService } from '../services/board-api.service';
import { UserService } from '../services/user.service';
import { BoardPreview, User } from '../interfaces';
import { MatSnackBar } from '@angular/material/snack-bar';
import { BoardUsersApiService } from '../services/board-users-api.service';
import { THIS_EXPR } from '@angular/compiler/src/output/output_ast';

@Component({
  selector: 'app-boards-list',
  templateUrl: './boards-list.component.html',
  styleUrls: ['./boards-list.component.css']
})
export class BoardsListComponent implements OnInit {
  public myBoards: BoardPreview[] = null;
  //temporary solution before we introduce guards
  private currentUserId: string;

  constructor(private dialog: MatDialog,
              private boardApiService: BoardApiService,
              private userService: UserService,
              private snackbar: MatSnackBar) { }

  ngOnInit(): void {
    this.boardApiService.myBoardsList().subscribe(boards => {
      this.myBoards = boards;
    });
    // "delete board" button only displayed for boards which are owned by currently logged in user
    // fetch id of the current user in order to check if some board was created by this user or not
    this.userService.getUser().subscribe(user => {
      this.currentUserId = user.id;
    })
  }

  /**
   * Opens a NewBoardComponent in a dialog in a "create" mode
   */
  public showNewBoardDialog(): void {
    this.dialog.open(NewBoardComponent);
  }

  /**
   * Creates a link to the board
   */
  public getBoardLink(boardPreview: BoardPreview) {
    return `/board/${boardPreview.id}`;
  }

  /**
   * Sends a request to delete a board
   */
  public deleteBoard(event: any, boardId: string, boardTitle: string) {
    // stop click event from opening the board
    event.stopPropagation();
    // ask user if they really want to delete a note
    const reallyWantToDelete = confirm(`Are you sure you want to delete board "${boardTitle}"?`);
    if (reallyWantToDelete) {
      this.boardApiService.deleteBoard(boardId).subscribe(() => {        
        // remove board from the list
        const deletedBoardIndex = this.myBoards.findIndex(board => board.id === boardId);
        if (deletedBoardIndex >= 0 && deletedBoardIndex < this.myBoards.length) {
          this.myBoards.splice(deletedBoardIndex, 1);
        }
        this.snackbar.open(`Board "${boardTitle}" was deleted`, 'Ok');
      }, err => {
        this.snackbar.open(`Error occurred while deleting board "${boardTitle}"`, 'Ok');
      });
    }
  }

  /**
   * Returns true if currently logged in user can delete given board
   */
  public canDeleteBoard(boardPreview: BoardPreview) {
    return boardPreview.ownerId === this.currentUserId;
  }
}
