import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AccessibleBoardsComponent } from './accessible-boards.component';

describe('AccessibleBoardsComponent', () => {
  let component: AccessibleBoardsComponent;
  let fixture: ComponentFixture<AccessibleBoardsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AccessibleBoardsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccessibleBoardsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
