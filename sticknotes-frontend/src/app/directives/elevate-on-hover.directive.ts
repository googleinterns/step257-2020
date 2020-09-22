// Copyright 2020 Google LLC

/**
 * This directive is responsible for changing elevation of element on mouse hover.
 * Elevation is meant as material design elevation, so it has influence on few 
 * properties of element, including the shadow it casts, which was desired effect
 * in our design. 
 */

import { Directive, ElementRef, HostListener, Input, Renderer2, SimpleChanges, OnChanges } from '@angular/core';

@Directive({
  selector: '[appElevateOnHover]'
})
export class ElevateOnHoverDirective implements OnChanges {

  @Input()
  defaultElevation = 2;

  @Input()
  raisedElevation = 8;

  constructor(
    private element: ElementRef,
    private renderer: Renderer2
  ) {
    this.setElevation(this.defaultElevation);
  }

  ngOnChanges(changes: SimpleChanges): void{
    this.setElevation(this.defaultElevation);
  }

  @HostListener('mouseenter')
  onMouseEnter(): void {
    this.setElevation(this.raisedElevation);
  }

  @HostListener('mouseleave')
  onMouseLeave(): void {
    this.setElevation(this.defaultElevation);
  }

  /**
   * 
   * @param amount elevation that should be set for the element
   * 
   * Function received as argument a number which indicates level of mat-elevation-z
   * and sets that property of the element. First of all it removes all classes that
   * sets the mat-elevation to remove old setting of that property or other interfering
   * settings, than it creates class of that property and adds it to the element.
   * This classes are fore example 'mat-elevation-z4', 'mat-elevation-z8'.
   */
  setElevation(amount: number): void {
    const elevationPrefix = 'mat-elevation-z';
    const classesToRemove = Array.from((<HTMLElement>this.element.nativeElement).classList)
      .filter(c => c.startsWith(elevationPrefix));
    classesToRemove.forEach((c) => {
      this.renderer.removeClass(this.element.nativeElement, c);
    });

    const newClass = `${elevationPrefix}${amount}`;
    this.renderer.addClass(this.element.nativeElement, newClass);
  }
}
