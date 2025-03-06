import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import {AuthService} from "./auth.service";
import {RegisterRequest} from "../interfaces/registerRequest.interface";
import {LoginRequest} from "../interfaces/loginRequest.interface";

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should register a user via POST', () => {
    const dummyRegisterRequest: RegisterRequest = {
      email: 'newuser@example.com',
      firstName: 'New',
      lastName: 'User',
      password: 'password123'
    };

    service.register(dummyRegisterRequest).subscribe(response => {
      expect(response).toBeUndefined();
    });

    const req = httpMock.expectOne('api/auth/register');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(dummyRegisterRequest);
    req.flush(null);
  });

  it('should login a user via POST and return session information', () => {
    const dummyLoginRequest: LoginRequest = {
      email: 'test@example.com',
      password: 'password123'
    };

    const dummySessionInformation: SessionInformation = {
      token: 'Token',
      type: 'Bearer',
      id: 1,
      username: 'dummyUser',
      firstName: 'John',
      lastName: 'Doe',
      admin: false
    };

    service.login(dummyLoginRequest).subscribe(sessionInfo => {
      expect(sessionInfo).toEqual(dummySessionInformation);
    });

    const req = httpMock.expectOne('api/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(dummyLoginRequest);
    req.flush(dummySessionInformation);
  });
});
