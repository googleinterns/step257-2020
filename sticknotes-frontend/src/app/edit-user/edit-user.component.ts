import { Component, OnInit, Inject } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { UserRole } from '../enums/user-role.enum';
import { BoardUsersApiService } from '../services/board-users-api.service';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-edit-user',
  templateUrl: './edit-user.component.html',
  styleUrls: ['./edit-user.component.css']
})
export class EditUserComponent implements OnInit {

  private previousUserRole: UserRole;
  private boardId: string;
  private roleId: string;

  public get userRole(): typeof UserRole {
    return UserRole;
  }

  public editUserForm: FormGroup;

  constructor(private boardUsersService: BoardUsersApiService,
    private dialogRef: MatDialogRef<EditUserComponent>,
    @Inject(MAT_DIALOG_DATA) data,
    private snackBar: MatSnackBar) {
    this.previousUserRole = data.role;
    this.boardId = data.boardId;
    this.roleId = data.roleId;

    this.editUserForm  = new FormGroup({
      role: new FormControl(this.previousUserRole.toString(), [
        Validators.required
      ])
    });
  }

  ngOnInit(): void {
  }

  editUser(): void {
    if (this.editUserForm.valid) {
      this.boardUsersService
        .editUserRole(this.boardId, this.roleId, this.editUserForm.controls.role.value)
        .subscribe(() => {
          this.snackBar.open("User role edited", "Ok", {
            duration: 2000,
          });
          this.dialogRef.close(this.editUserForm.controls.role.value);
        }, err => {
          // something went wrong
          this.snackBar.open("Error while editing user role."
            , "Ok", {
            duration: 2000,
          });
          this.dialogRef.close(this.previousUserRole);
        });
    }
  }
}
