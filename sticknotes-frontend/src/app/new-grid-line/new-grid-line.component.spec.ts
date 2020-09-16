import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NewGridLineComponent } from './new-grid-line.component';

describe('NewGridLineComponent', () => {
  let component: NewGridLineComponent;
  let fixture: ComponentFixture<NewGridLineComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NewGridLineComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NewGridLineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
