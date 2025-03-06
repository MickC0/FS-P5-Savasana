import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { LoginComponent } from './login.component';
import {of, throwError} from "rxjs";
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: any;
  let router: any;
  let sessionService: any;

  beforeEach(async () => {
    const authServiceMock = {
      login: jest.fn()
    };

    const routerMock = {
      navigate: jest.fn()
    };

    const sessionServiceMock = {
      logIn: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock },
        { provide: SessionService, useValue: sessionServiceMock }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
    sessionService = TestBed.inject(SessionService);
    fixture.detectChanges();
  });

  it('should create the component and initialize the form', () => {
    expect(component).toBeTruthy();
    expect(component.form).toBeDefined();
    expect(component.form.get('email')).toBeDefined();
    expect(component.form.get('password')).toBeDefined();
    expect(component.hide).toBe(true);
    expect(component.onError).toBe(false);
  });

  describe('submit', () => {
    const validLoginRequest = {
      email: 'user@example.com',
      password: 'password123'
    };

    it('should call authService.login, log in and navigate on success', () => {
      component.form.setValue(validLoginRequest);
      const fakeSessionInfo = {
        token: 'dummy-token',
        type: 'dummy',
        id: 1,
        username: 'dummy',
        firstName: 'Dummy',
        lastName: 'User',
        admin: false
      };
      authService.login.mockReturnValue(of(fakeSessionInfo));
      component.submit();
      expect(authService.login).toHaveBeenCalledWith(validLoginRequest);
      expect(sessionService.logIn).toHaveBeenCalledWith(fakeSessionInfo);
      expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
      expect(component.onError).toBe(false);
    });

    it('should set onError to true if authService.login fails', () => {
      component.form.setValue(validLoginRequest);
      authService.login.mockReturnValue(throwError(() => new Error('Login failed')));
      component.submit();
      expect(authService.login).toHaveBeenCalledWith(validLoginRequest);
      expect(component.onError).toBe(true);
    });
  });
});
