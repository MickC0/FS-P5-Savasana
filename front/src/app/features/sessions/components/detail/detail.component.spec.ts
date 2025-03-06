import {ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {FormBuilder} from '@angular/forms';
import {MatSnackBar} from '@angular/material/snack-bar';
import { expect } from '@jest/globals';
import { SessionService } from '../../../../services/session.service';

import { DetailComponent } from './detail.component';
import {ActivatedRoute, Router} from "@angular/router";
import {SessionApiService} from "../../services/session-api.service";
import {TeacherService} from "../../../../services/teacher.service";
import {Session} from "../../interfaces/session.interface";
import {Teacher} from "../../../../interfaces/teacher.interface";
import {of} from "rxjs";


describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let router: any;
  let activatedRoute: any;
  let sessionApiService: any;
  let teacherService: any;
  let snackBar: any;
  let sessionService: any;

  beforeEach(async () => {
    const routerMock = { navigate: jest.fn() };
    const activatedRouteMock = {
      snapshot: { paramMap: { get: jest.fn().mockReturnValue('123') } }
    };
    const sessionApiServiceMock = {
      detail: jest.fn(),
      delete: jest.fn(),
      participate: jest.fn(),
      unParticipate: jest.fn()
    };
    const teacherServiceMock = {
      detail: jest.fn()
    };
    const snackBarMock = { open: jest.fn() };
    const sessionServiceMock = {
      sessionInformation: {
        token: 'dummy-token',
        id: 1,
        admin: true,
        type: 'dummy',
        username: 'dummy',
        firstName: 'Dummy',
        lastName: 'User'
      }
    };

    await TestBed.configureTestingModule({
      declarations: [DetailComponent],
      providers: [
        FormBuilder,
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: SessionApiService, useValue: sessionApiServiceMock },
        { provide: TeacherService, useValue: teacherServiceMock },
        { provide: MatSnackBar, useValue: snackBarMock },
        { provide: Router, useValue: routerMock },
        { provide: SessionService, useValue: sessionServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    sessionApiService = TestBed.inject(SessionApiService);
    teacherService = TestBed.inject(TeacherService);
    snackBar = TestBed.inject(MatSnackBar);
    sessionService = TestBed.inject(SessionService);
  });

  describe('Constructor & Initialisation', () => {
    it('should set sessionId, isAdmin and userId from constructor', () => {
      expect(component.sessionId).toBe('123');
      expect(component.isAdmin).toBe(true);
      expect(component.userId).toBe('1');
    });

    it('should fetch session on ngOnInit', fakeAsync(() => {
      const fakeSession: Session = {
        name: 'Session Test',
        description: 'Description test',
        date: new Date('2022-01-01'),
        teacher_id: 1,
        users: [1, 2]
      };
      const fakeTeacher: Teacher = {
        id: 1,
        firstName: 'John',
        lastName: 'Doe',
        createdAt: new Date(),
        updatedAt: new Date()
      };

      sessionApiService.detail.mockReturnValue(of(fakeSession));
      teacherService.detail.mockReturnValue(of(fakeTeacher));

      component.ngOnInit();
      tick();

      expect(sessionApiService.detail).toHaveBeenCalledWith(component.sessionId);
      expect(component.session).toEqual(fakeSession);
      expect(component.isParticipate).toBe(true);
      expect(teacherService.detail).toHaveBeenCalledWith(fakeSession.teacher_id.toString());
      expect(component.teacher).toEqual(fakeTeacher);
    }));
  });

  describe('Méthodes d\'actions', () => {
    it('should delete session and navigate', () => {
      sessionApiService.delete.mockReturnValue(of({}));
      component.delete();
      expect(sessionApiService.delete).toHaveBeenCalledWith(component.sessionId);
      expect(snackBar.open).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });
      expect(router.navigate).toHaveBeenCalledWith(['sessions']);
    });

    it('should participate and refresh session', () => {
      sessionApiService.participate.mockReturnValue(of(void 0));
      // On espionne la méthode privée fetchSession
      const fetchSessionSpy = jest.spyOn(component as any, 'fetchSession');
      component.participate();
      expect(sessionApiService.participate).toHaveBeenCalledWith(component.sessionId, component.userId);
      expect(fetchSessionSpy).toHaveBeenCalled();
    });

    it('should unParticipate and refresh session', () => {
      sessionApiService.unParticipate.mockReturnValue(of(void 0));
      const fetchSessionSpy = jest.spyOn(component as any, 'fetchSession');
      component.unParticipate();
      expect(sessionApiService.unParticipate).toHaveBeenCalledWith(component.sessionId, component.userId);
      expect(fetchSessionSpy).toHaveBeenCalled();
    });
  });

  describe('Méthode back', () => {
    it('should call window.history.back on back()', () => {
      const historyBackSpy = jest.spyOn(window.history, 'back').mockImplementation(() => {});
      component.back();
      expect(historyBackSpy).toHaveBeenCalled();
      historyBackSpy.mockRestore();
    });
  });
});
