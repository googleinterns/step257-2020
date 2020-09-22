// Copyright 2020 Google LLC

import { TestBed } from '@angular/core/testing';

import { SharedBoardService } from './shared-board.service';

describe('SharedBoardService', () => {
  let service: SharedBoardService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SharedBoardService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
