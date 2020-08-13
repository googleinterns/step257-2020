import { Component, OnInit } from '@angular/core';
import { CdkDragEnd } from '@angular/cdk/drag-drop';
import { Vector2 } from '../utility/vector';
import { getTranslateValues } from '../utility/util';
import { Note, Board } from '../interfaces';
import { ApiService } from '../services/api.service';

@Component({
  selector: 'app-board',
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.css']
})
export class BoardComponent implements OnInit {
  public board: Board;
  public readonly NOTE_WIDTH = 200;
  public readonly NOTE_HEIGHT = 250;

  constructor(private apiService: ApiService) {
    // load board
    this.apiService.getBoard('boardKey').subscribe(board => { 
      this.board = board;
    });
  }

  ngOnInit(): void {

  }

  // moves a note to a proper position after it was released
  public onNoteDrop(cdkDragEnd: CdkDragEnd, note: Note) {
    const elementRef = cdkDragEnd.source.element.nativeElement;
    const curTranslate = getTranslateValues(elementRef);
    const x = note.x + curTranslate.x;
    const y = note.y + curTranslate.y;
    // update position temprarily so that it is not excluded from vectors list
    note.x = x;
    note.y = y;
    const vectors = [];
    // get the list of all possibly correct positions of the note
    for (let i = 0; i < this.board.rows; ++i) {
      for (let j = 0; j < this.board.cols; ++j) {
        vectors.push(new Vector2(i * this.NOTE_WIDTH, j * this.NOTE_HEIGHT));
      }
    }
    // exclude already taken positions
    const epsilon = 1;
    this.board.notes.forEach(note => {
      const notePosition = new Vector2(note.x, note.y);
      // allow some margin of error, so distance shouldn't be exactly equal
      const taken = vectors.find((v: Vector2) => v.distanceTo(notePosition) <= epsilon);
      if (taken) {
        const indexOfTaken = vectors.indexOf(taken);
        vectors.splice(indexOfTaken, 1);
      }
    });
    // find the shortest vector which is going to be the closest correct note position
    let minDistanceVector = vectors[0];
    const curDragPos = new Vector2(x, y);
    for (const v of vectors) {
      // calculate distance to the point, find the vector with the minimal distance to the point
      if (curDragPos.distanceTo(v) < curDragPos.distanceTo(minDistanceVector)) {
        minDistanceVector = v;
      }
    }
    // apply new transformation
    note.x = minDistanceVector.x;
    note.y = minDistanceVector.y;
    cdkDragEnd.source._dragRef.reset();
    elementRef.style.transform = '';
  }
}
