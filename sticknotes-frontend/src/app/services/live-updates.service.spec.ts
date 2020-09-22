// Copyright 2020 Google LLC

import { TestBed } from '@angular/core/testing';

import { LiveUpdatesService } from './live-updates.service';

describe('LiveUpdatesService', () => {
  let service: LiveUpdatesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LiveUpdatesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
