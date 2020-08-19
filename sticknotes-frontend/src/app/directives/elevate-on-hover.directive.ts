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
