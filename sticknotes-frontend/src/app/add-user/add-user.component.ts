/**
 * This component is a dialog that is opened when users wants to add
 * new user to the board. It receives data from the user using form and 
 * than sends that data to the server using boardUsersService. 
 */
import { Component, OnInit, Input, Inject } from '@angular/core';
import { UserRole } from '../enums/user-role.enum';
import { UserBoardRole, User } from '../interfaces';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { BoardUsersApiService } from '../services/board-users-api.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-add-user',
  templateUrl: './add-user.component.html',
  styleUrls: ['./add-user.component.css']
})
export class AddUserComponent implements OnInit {

  @Input() boardId: string;

  public get userRole(): typeof UserRole {
    return UserRole;
  }

  public addUserForm = new FormGroup({
    userEmail: new FormControl('', [
      Validators.required,
      Validators.email
    ]),
    role: new FormControl(UserRole.USER.toString(), [
      Validators.required
    ])
  });

  constructor(private boardUsersService: BoardUsersApiService,
    private dialogRef: MatDialogRef<AddUserComponent>,
    @Inject(MAT_DIALOG_DATA) data,
    private snackBar: MatSnackBar) {
    this.boardId = data.boardId;
  }

  ngOnInit(): void {
  }
  
  /**
   * Uses boardUsersService to send POST request to api/board/users/:id endpoint which adds user to the board.
   * Server response contains successfully added user. When the request was successful newly created user
   * is passed to component which opened this dialog.
   */
  addUser(): void {
    if (this.addUserForm.valid) {
      // load board
      this.boardUsersService
        .addBoardUser(this.boardId, this.addUserForm.controls.userEmail.value, this.addUserForm.controls.role.value)
        .subscribe(user => {
          this.dialogRef.close(user);
          this.snackBar.open("User added", "Ok", {
            duration: 2000,
          });
        }, err => {
          // something went wrong
          this.snackBar.open("Error while adding user."
            , "Ok", {
            duration: 2000,
          });
          this.dialogRef.close();
        });
    }
  }

}
