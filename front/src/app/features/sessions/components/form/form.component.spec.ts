import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';
import { SessionApiService } from '../../services/session-api.service';
import { FormComponent } from './form.component';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { TeacherService } from '../../../../services/teacher.service';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let router: any;
  let activatedRoute: any;
  let sessionApiService: any;
  let snackBar: any;
  let sessionService: any;
  let teacherService: any;

  beforeEach(async () => {
    const routerMock = {
      navigate: jest.fn(),
      url: '/sessions/create'
    };

    const activatedRouteMock = {
      snapshot: {
        paramMap: {
          get: jest.fn().mockReturnValue(null)
        }
      }
    };

    const sessionApiServiceMock = {
      detail: jest.fn(),
      create: jest.fn(),
      update: jest.fn()
    };

    const snackBarMock = {
      open: jest.fn()
    };

    const sessionServiceMock = {
      sessionInformation: {
        token: 'dummy-token',
        type: 'dummy',
        id: 1,
        username: 'dummy',
        firstName: 'Dummy',
        lastName: 'User',
        admin: true
      }
    };

    const teacherServiceMock = {
      all: jest.fn().mockReturnValue(of([]))
    };

    await TestBed.configureTestingModule({
      declarations: [FormComponent],
      imports: [ReactiveFormsModule],
      providers: [
        FormBuilder,
        { provide: Router, useValue: routerMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: SessionApiService, useValue: sessionApiServiceMock },
        { provide: MatSnackBar, useValue: snackBarMock },
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: TeacherService, useValue: teacherServiceMock }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    sessionApiService = TestBed.inject(SessionApiService);
    snackBar = TestBed.inject(MatSnackBar);
    sessionService = TestBed.inject(SessionService);
    teacherService = TestBed.inject(TeacherService);
    fixture.detectChanges();
  });

  describe('Admin user', () => {
    beforeEach(() => {
      sessionService.sessionInformation.admin = true;
      router.url = '/sessions/create';
    });

    it('should create the component', () => {
      expect(component).toBeTruthy();
    });

    it('should initialize form in creation mode (ngOnInit without "update" in URL)', () => {
      component.ngOnInit();
      expect(component.onUpdate).toBe(false);
      expect(component.sessionForm).toBeDefined();
      expect(component.sessionForm?.get('name')).toBeDefined();
      expect(component.sessionForm?.get('date')).toBeDefined();
      expect(component.sessionForm?.get('teacher_id')).toBeDefined();
      expect(component.sessionForm?.get('description')).toBeDefined();
    });

    it('should initialize form in update mode (ngOnInit with "update" in URL)', fakeAsync(() => {
      router.url = '/sessions/update/123';
      activatedRoute.snapshot.paramMap.get.mockReturnValue('123');
      const mockSession = {
        name: 'Test Session',
        date: new Date('2022-01-01'),
        teacher_id: 1,
        description: 'Test description',
        users: []
      };
      sessionApiService.detail.mockReturnValue(of(mockSession));
      component.ngOnInit();
      tick();
      expect(component.onUpdate).toBe(true);
      expect(sessionApiService.detail).toHaveBeenCalledWith('123');

      const nameControl = component.sessionForm?.get('name');
      const dateControl = component.sessionForm?.get('date');
      const teacherIdControl = component.sessionForm?.get('teacher_id');
      const descriptionControl = component.sessionForm?.get('description');

      expect(nameControl?.value).toBe('Test Session');
      expect(dateControl?.value).toBe(new Date('2022-01-01').toISOString().split('T')[0]);
      expect(teacherIdControl?.value).toBe(1);
      expect(descriptionControl?.value).toBe('Test description');
    }));

    it('should submit and create a session when not updating', () => {
      component.onUpdate = false;
      component.sessionForm = component['fb'].group({
        name: 'New Session',
        date: '2022-01-01',
        teacher_id: 1,
        description: 'New description'
      });
      const formValue = component.sessionForm.value;
      sessionApiService.create.mockReturnValue(of(formValue));
      component.submit();
      expect(sessionApiService.create).toHaveBeenCalledWith(formValue);
      expect(snackBar.open).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
      expect(router.navigate).toHaveBeenCalledWith(['sessions']);
    });

    it('should submit and update a session when in update mode', () => {
      component.onUpdate = true;
      (component as any).id = '123';
      component.sessionForm = component['fb'].group({
        name: 'Updated Session',
        date: '2022-02-02',
        teacher_id: 2,
        description: 'Updated description'
      });
      const formValue = component.sessionForm.value;
      sessionApiService.update.mockReturnValue(of(formValue));
      component.submit();
      expect(sessionApiService.update).toHaveBeenCalledWith('123', formValue);
      expect(snackBar.open).toHaveBeenCalledWith('Session updated !', 'Close', { duration: 3000 });
      expect(router.navigate).toHaveBeenCalledWith(['sessions']);
    });

    it('should handle submit gracefully if sessionForm is undefined', () => {
      component.sessionForm = undefined;
      sessionApiService.create.mockReturnValue(of({}));
      expect(() => component.submit()).not.toThrow();
    });
  });

  describe('Non-admin user', () => {
    beforeEach(() => {
      sessionService.sessionInformation.admin = false;
    });

    it('should redirect non-admin users in ngOnInit', () => {
      component.ngOnInit();
      expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
    });
  });
});
