import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { TeacherService } from './teacher.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {Teacher} from "../interfaces/teacher.interface";

describe('TeacherService', () => {
  let service: TeacherService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TeacherService]
    });
    service = TestBed.inject(TeacherService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });


  it('should fetch all teachers via GET', () => {
    const dummyTeachers: Teacher[] = [
      {
        id: 1,
        firstName: 'John',
        lastName: 'Doe',
        createdAt: new Date('2023-01-01T00:00:00Z'),
        updatedAt: new Date('2023-01-02T00:00:00Z')
      },
      {
        id: 2,
        firstName: 'Jane',
        lastName: 'Smith',
        createdAt: new Date('2023-02-01T00:00:00Z'),
        updatedAt: new Date('2023-02-02T00:00:00Z')
      }
    ];

    service.all().subscribe((teachers) => {
      expect(teachers.length).toBe(2);
      expect(teachers).toEqual(dummyTeachers);
    });

    const req = httpMock.expectOne('api/teacher');
    expect(req.request.method).toBe('GET');
    req.flush(dummyTeachers);
  });

  it('should fetch teacher detail via GET', () => {
    const dummyTeacher: Teacher = {
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      createdAt: new Date('2023-01-01T00:00:00Z'),
      updatedAt: new Date('2023-01-02T00:00:00Z')
    };

    service.detail('1').subscribe((teacher) => {
      expect(teacher).toEqual(dummyTeacher);
    });

    const req = httpMock.expectOne('api/teacher/1');
    expect(req.request.method).toBe('GET');
    req.flush(dummyTeacher);
  });
});
