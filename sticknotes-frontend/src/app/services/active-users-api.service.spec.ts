import { TestBed } from '@angular/core/testing';

import { ActiveUsersApiService } from './active-users-api.service';

describe('ActiveUsersApiService', () => {
  let service: ActiveUsersApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ActiveUsersApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
