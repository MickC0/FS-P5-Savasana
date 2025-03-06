import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionApiService } from './session-api.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {Session} from "../interfaces/session.interface";

describe('SessionApiService', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;

  const dummySession1: Session = {
    id: 1,
    name: 'Updated Session',
    description: 'Updated Description',
    date: new Date('2023-01-01T00:00:00Z'),
    teacher_id: 1,
    users: [1, 2],
    createdAt: new Date('2023-01-01T00:00:00Z'),
    updatedAt: new Date('2023-04-01T00:00:00Z')
  };

  const dummySession2: Session = {
    id: 2,
    name: 'Session Two',
    description: 'Description Two',
    date: new Date('2023-02-01T00:00:00Z'),
    teacher_id: 2,
    users: [2, 3],
    createdAt: new Date('2023-02-01T00:00:00Z'),
    updatedAt: new Date('2023-02-02T00:00:00Z')
  };

  const dummySessions: Session[] = [dummySession1, dummySession2];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [SessionApiService]
    });
    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch all sessions via GET', () => {
    service.all().subscribe((sessions) => {
      expect(sessions.length).toBe(2);
      expect(sessions).toEqual(dummySessions);
    });

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('GET');
    req.flush(dummySessions);
  });

  it('should fetch session detail via GET', () => {
    service.detail('1').subscribe((session) => {
      expect(session).toEqual(dummySession1);
    });

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('GET');
    req.flush(dummySession1);
  });

  it('should delete a session via DELETE', () => {
    service.delete('1').subscribe((response) => {
      expect(response).toEqual({});
    });

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });

  it('should create a session via POST', () => {
    // Pour la création, on ne fournit pas d'id, createdAt, ni updatedAt
    const newSession: Session = {
      name: 'New Session',
      description: 'New Description',
      date: new Date('2023-03-01T00:00:00Z'),
      teacher_id: 3,
      users: []
    };

    // On simule la réponse du serveur avec un objet complet (avec id, createdAt, updatedAt)
    const createdSession: Session = {
      ...newSession,
      id: 3,
      createdAt: new Date('2023-03-01T00:00:00Z'),
      updatedAt: new Date('2023-03-01T00:00:00Z')
    };

    service.create(newSession).subscribe((session) => {
      expect(session).toEqual(createdSession);
    });

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newSession);
    req.flush(createdSession);
  });

  it('should update a session via PUT', () => {
    service.update('1', dummySession1).subscribe((session) => {
      expect(session).toEqual(dummySession1);
    });

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(dummySession1);
    req.flush(dummySession1);
  });

  it('should participate in a session via POST', () => {
    service.participate('1', '10').subscribe((response) => {
      expect(response).toBeNull();
    });

    const req = httpMock.expectOne('api/session/1/participate/10');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toBeNull();
    req.flush(null);
  });

  it('should unParticipate in a session via DELETE', () => {
    service.unParticipate('1', '10').subscribe((response) => {
      expect(response).toBeNull();
    });

    const req = httpMock.expectOne('api/session/1/participate/10');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
