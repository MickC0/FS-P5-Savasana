import {ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import { SessionService } from 'src/app/services/session.service';
import { expect } from '@jest/globals'
import { MeComponent } from './me.component';
import {Router} from "@angular/router";
import {MatSnackBar} from "@angular/material/snack-bar";
import {UserService} from "../../services/user.service";
import {of} from "rxjs";
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let router: any;
  let matSnackBar: any;
  let userService: any;
  let sessionService: any;

  const fakeUser = {
    id: 1,
    email: 'test@example.com',
    lastName: 'Doe',
    firstName: 'John',
    admin: false,
    password: 'pass',
    createdAt: new Date(),
    updatedAt: new Date()
  };

  beforeEach(async () => {
    const routerMock = { navigate: jest.fn() };
    const matSnackBarMock = { open: jest.fn() };
    const userServiceMock = {
      getById: jest.fn(),
      delete: jest.fn()
    };
    const sessionServiceMock = {
      sessionInformation: {
        token: 'dummy-token',
        id: 1,
        username: 'dummy',
        firstName: 'Dummy',
        lastName: 'User',
        admin: false
      },
      logOut: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      providers: [
        { provide: Router, useValue: routerMock },
        { provide: MatSnackBar, useValue: matSnackBarMock },
        { provide: UserService, useValue: userServiceMock },
        { provide: SessionService, useValue: sessionServiceMock }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    matSnackBar = TestBed.inject(MatSnackBar);
    userService = TestBed.inject(UserService);
    sessionService = TestBed.inject(SessionService);
  });

  describe('ngOnInit', () => {
    it('should fetch user by id and assign it to component.user', fakeAsync(() => {
      userService.getById.mockReturnValue(of(fakeUser));
      fixture.detectChanges();
      tick();
      expect(userService.getById).toHaveBeenCalledWith(sessionService.sessionInformation.id.toString());
      expect(component.user).toEqual(fakeUser);
    }));
  });

  describe('back', () => {
    it('should call window.history.back()', () => {
      const historyBackSpy = jest.spyOn(window.history, 'back').mockImplementation(() => {});
      component.back();
      expect(historyBackSpy).toHaveBeenCalled();
      historyBackSpy.mockRestore();
    });
  });

  describe('delete', () => {
    it('should delete user, show snackbar, log out and navigate to "/"', fakeAsync(() => {
      userService.getById.mockReturnValue(of(fakeUser));
      fixture.detectChanges();
      tick();
      userService.delete.mockReturnValue(of({}));
      component.delete();
      tick();
      expect(userService.delete).toHaveBeenCalledWith(sessionService.sessionInformation.id.toString());
      expect(matSnackBar.open).toHaveBeenCalledWith("Your account has been deleted !", 'Close', { duration: 3000 });
      expect(sessionService.logOut).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(['/']);
    }));
  });
});
