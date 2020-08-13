import { Component, OnInit } from '@angular/core';
import { CdkDragEnd } from '@angular/cdk/drag-drop';
import { Vector2 } from '../utility/vector';
import { getTranslateValues } from '../utility/util';
import { Note } from '../interfaces';

@Component({
  selector: 'app-board',
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.css']
})
export class BoardComponent implements OnInit {

  public notes: Note[] = [];
  public boardTitle = 'Notesboard';
  public readonly BOARD_WIDTH = 6;
  public readonly BOARD_HEIGHT = 4;
  constructor() { }

  ngOnInit(): void {
    // generate mock notes
    for (let i = 0; i < 5; ++i) {
      this.notes.push({ 
        x: this.getRandomInt(6) * 150,
        y: this.getRandomInt(4) * 200,
        translateX: 0,
        translateY: 0
      });
    }
  }

  // returns a random int in the range [0, max)
  private getRandomInt(max: number) {
    return Math.floor(Math.random() * Math.floor(max));
  }

  // moves a note to a proper position after it was released
  public onNoteDrop(cdkDragEnd: CdkDragEnd, note: Note) {
    const elementRef = cdkDragEnd.source.element.nativeElement;
    const curTranslate = getTranslateValues(elementRef);
    note.translateX = curTranslate.x;
    note.translateY = curTranslate.y;
    const x = note.x + note.translateX;
    const y = note.y + note.translateY;
    const vectors = [];
    // get the list of all possibly correct positions of the note
    for (let i = 0; i < this.BOARD_WIDTH; ++i) {
      for (let j = 0; j < this.BOARD_HEIGHT; ++j) {
        vectors.push(new Vector2(i * 150, j * 200));
      }
    }
    // exclude already taken positions
    const epsilon = 1;
    this.notes.forEach(note => {
      const notePosition = new Vector2(note.x + note.translateX, note.y + note.translateY);
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
    note.translateX += (minDistanceVector.x - x);
    note.translateY += (minDistanceVector.y - y);
    const transform = `translate3d(${note.translateX}px, ${note.translateY}px, 0)`;
    elementRef.style.transform = transform;
  }
}
