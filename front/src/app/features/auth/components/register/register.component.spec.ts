import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { expect } from '@jest/globals';

import { RegisterComponent } from './register.component';
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";
import {of, throwError} from "rxjs";
import {CUSTOM_ELEMENTS_SCHEMA} from "@angular/core";

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: any;
  let router: any;

  beforeEach(async () => {
    const authServiceMock = {
      register: jest.fn()
    };

    const routerMock = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [RegisterComponent],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create the component and initialize the form', () => {
    expect(component).toBeTruthy();
    // Vérifier que le formulaire et ses contrôles sont définis
    const form = component.form;
    expect(form).toBeDefined();
    expect(form.get('email')).toBeDefined();
    expect(form.get('firstName')).toBeDefined();
    expect(form.get('lastName')).toBeDefined();
    expect(form.get('password')).toBeDefined();
  });

  describe('submit', () => {
    const validRegisterRequest = {
      email: 'test@example.com',
      firstName: 'John',
      lastName: 'Doe',
      password: '123456'
    };

    it('should call authService.register and navigate to "/login" on success', () => {
      component.form.setValue(validRegisterRequest);
      authService.register.mockReturnValue(of(void 0));
      component.submit();
      expect(authService.register).toHaveBeenCalledWith(validRegisterRequest);
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
      expect(component.onError).toBe(false);
    });

    it('should set onError to true if authService.register fails', () => {
      component.form.setValue(validRegisterRequest);
      authService.register.mockReturnValue(throwError(() => new Error('Registration failed')));
      component.submit();
      expect(authService.register).toHaveBeenCalledWith(validRegisterRequest);
      expect(component.onError).toBe(true);
    });
  });
});
