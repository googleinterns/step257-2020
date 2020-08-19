import { TestBed } from '@angular/core/testing';

import { UserService } from './board-users-api.service';

describe('BoardUsersApiService', () => {
  let service: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
