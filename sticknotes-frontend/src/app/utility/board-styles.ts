// Copyright 2020 Google LLC

import { BoardGridLine, Note } from '../interfaces';

/**
 * Class with methods to style a board layout
 */
export class BoardStyles {
  public readonly NOTE_WIDTH = 200;
  public readonly NOTE_HEIGHT = 250;
  public readonly MARGIN_BETWEEN_ADJ_NOTES = 6; // margin between adjacent notes is 6px
  public readonly COLUMN_NAME_PADDING = 6; // padding of the column name element
  public readonly ADD_NEW_COLUMN_BUTTON_WIDTH = 36; // width of the "new column" button

  private rows: number;
  private cols: number;

  constructor(cols: number, rows: number) {
    this.cols = cols;
    this.rows = rows;
  }

  public getBoardWidth() {
    return `width:${this.NOTE_WIDTH * this.cols}px;`;
  }

  public getBoardHeight() {
    return `height:${this.NOTE_HEIGHT * this.rows}px;`;
  }

  public getBoardStyle() {
    return `${this.getBoardWidth()} ${this.getBoardHeight()}`;
  }

  public getBoardWrapperWidth() {
    return `width: min(100% - 80px, ${this.NOTE_WIDTH * this.cols}px);`;
  }

  public getBoardWrapperHeight() {
    return `height: min(100% - 70px, ${this.NOTE_HEIGHT * this.rows}px);`;
  }

  public getRCWrapperWidth() {
    return `width: min(100%, ${(this.NOTE_WIDTH * this.cols) + 80}px);`;
  }

  public getRCWrapperHeight() {
    return `height: min(100% - 40px, ${(this.NOTE_HEIGHT * this.rows) + 70}px);`;
  }

  public getRCWrapperStyle() {
    return `${this.getRCWrapperWidth()} ${this.getRCWrapperHeight()}`
  }

  public getColumnDivStyle(el: BoardGridLine) {
    // the width of the columns header is the width of columns - left and right margin, which is equal to margin between adjacent notes
    return `left: ${el.rangeStart * this.NOTE_WIDTH}px; width: ${(Math.abs(el.rangeEnd - el.rangeStart) * this.NOTE_WIDTH) - this.MARGIN_BETWEEN_ADJ_NOTES - this.COLUMN_NAME_PADDING}px;`;
  }

  public getBoardWrapperStyle() {
    // if board is wider than 100% of the screen or higher than 100%, set fixed width and height
    return `${this.getBoardWrapperWidth()} ${this.getBoardWrapperHeight()}`;
  }

  public getPlusButtonStyle(pos: number) {
    return `left: ${pos * this.NOTE_WIDTH}px;`
  }

  /**
   * Generates a correct style to position the note
   */
  public getNoteStyle(note: Note): string {
    return `left:${note.x * this.NOTE_WIDTH}px;top:${note.y * this.NOTE_HEIGHT}px`;
  }

  /** 
   * Generates a correct style to position the slot
   */
  public getSlotStyle(x: number, y: number): string {
    return `left:${x * this.NOTE_WIDTH}px;top:${y * this.NOTE_HEIGHT}px`;
  }
}

