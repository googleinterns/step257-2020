import { Component, OnInit } from '@angular/core';
import { UserService } from '../services/user.service';
import { BoardUsersApiService } from '../services/board-users-api.service';
import { UserBoardRole, User } from '../interfaces';
import { Observable, of, forkJoin, from } from 'rxjs';
import { UserRole } from '../models/user-role.enum';
import { take } from 'rxjs/operators';
import { MatDialog } from '@angular/material/dialog';
import { AddUserComponent } from '../add-user/add-user.component';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit {

  public adminView = false;
  public usersWithRole: UserBoardRole[] = [];
  public currentUser: User;

  constructor(private userService: UserService, 
              private boardUsersService: BoardUsersApiService,
              private dialog: MatDialog) { }

  ngOnInit(): void {
    forkJoin(
    [
      this.userService.getUser().pipe(take(1)),
      this.boardUsersService.getBoardUsers('boardKey')
    ]
    ).subscribe(([user, users]) => {
      console.log('1')
      this.currentUser = user;
      this.usersWithRole = users;
      const index = users.findIndex(userWithRole => user.key === userWithRole.user.key && userWithRole.role === UserRole.ADMIN);
      if (index === -1) {
        this.adminView = false;
      }else{
        this.adminView = true;
      }
    });
  }

  openAddUserDialog() {
    const dialogRef = this.dialog.open(AddUserComponent);
    dialogRef.afterClosed().subscribe(user => {
      // receive a new note here and add it to the board
      // data maybe undefined
      if (user) {
        this.usersWithRole.push(user);
      }
    })
  }
}
