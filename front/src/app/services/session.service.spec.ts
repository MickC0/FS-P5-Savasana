import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionService } from './session.service';
import {SessionInformation} from "../interfaces/sessionInformation.interface";

describe('SessionService', () => {
  let service: SessionService;
  let sessionInfoMock:SessionInformation;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
    sessionInfoMock = {
      token: 'dummyToken',
      type: 'dummyType',
      id: 1,
      username: 'dummyUser',
      firstName: 'John',
      lastName: 'Doe',
      admin: false,
    };
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should log in the user and call next', () => {
    const spyNext = jest.spyOn(service['isLoggedSubject'], 'next');
    service.logIn(sessionInfoMock);
    expect(service.sessionInformation).toEqual(sessionInfoMock);
    expect(service.isLogged).toBe(true);
    expect(spyNext).toHaveBeenCalledWith(true);
  });

  it('should log out the user and call next', () => {
    service.logIn(sessionInfoMock);
    const spyNext = jest.spyOn(service['isLoggedSubject'], 'next');
    service.logOut();

    expect(service.sessionInformation).toBeUndefined();
    expect(service.isLogged).toBe(false);
    expect(spyNext).toHaveBeenCalledWith(false);
  });

  it('should return an observable for isLogged', (done) => {
    service.$isLogged().subscribe((isLogged) => {
      expect(isLogged).toBe(false);

      service.logIn(sessionInfoMock);

      service.$isLogged().subscribe((newIsLogged) => {
        expect(newIsLogged).toBe(true);
        done();
      });
    });
  });
});
