/**
 * Helper class to store a cartesian coordinate point.
 */
export class Vector2 {
  public x: number;
  public y: number;

  constructor(x: number, y: number) {
    this.x = x;
    this.y = y;
  }

  /**
   * Calculates the distance to the other point
   */
  public distanceTo(other: Vector2): number {
    return Math.sqrt((this.x - other.x) * (this.x - other.x) + (this.y - other.y) * (this.y - other.y));
  }

  /**
   * Adds the coordinates of the other point to this
   */
  public add(other: Vector2): Vector2 {
    return new Vector2(this.x + other.x, this.y + other.y);
  }
}
