/**
 * A main view of the app, container that holds the board
 */
import { Component, OnInit } from '@angular/core';
import { MatDrawer } from '@angular/material/sidenav';
import { Router } from '@angular/router';

@Component({
  selector: 'app-board-container',
  templateUrl: './board-container.component.html',
  styleUrls: ['./board-container.component.css']
})
export class BoardContainerComponent implements OnInit {

  public iconName = 'menu';
 
  constructor(private router: Router) { }

  ngOnInit(): void {
  }

  // toggles the side menu, changes the icon name accordingly to the state
  public toggleMenu(drawer: MatDrawer) {
    drawer.toggle();
    if (this.iconName === 'menu') {
      this.iconName = 'menu_open';
    } else {
      this.iconName = 'menu';
    }
  }

  public backToBoards(): void {
    this.router.navigateByUrl('/boards');
  }
}
