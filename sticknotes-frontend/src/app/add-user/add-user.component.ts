import { Component, OnInit } from '@angular/core';
import { UserRole } from '../enums/user-role.enum';
import { UserBoardRole, User } from '../interfaces';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { BoardUsersApiService } from '../services/board-users-api.service';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-add-user',
  templateUrl: './add-user.component.html',
  styleUrls: ['./add-user.component.css']
})
export class AddUserComponent implements OnInit {

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
              private dialogRef: MatDialogRef<AddUserComponent>) { }

  ngOnInit(): void {
  }

  addUser(): void {
    const mockUser: User = {
      key: 'key123',
      nickname: 'ola',
      email: 'ola@google.com',
      accessibleBoards: []
    };

    const mockUserWithRole: UserBoardRole  = {
      user: mockUser,
      boardId: 'board1',
      role: UserRole.USER
    };

    if (this.addUserForm.valid) {
      this.boardUsersService
          .addBoardUser(this.addUserForm.controls.userEmail.value, this.addUserForm.controls.role.value)
          .subscribe(user => {
            this.dialogRef.close(user);
      }, err => {
        // something went wrong
        alert('Error occurred');
        this.dialogRef.close();
      });
    }
  }

}
