/// <reference types="cypress" />

describe('List tests', () => {

  const loginAsAdmin = () => {
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName2',
        firstName: 'firstName2',
        lastName: 'lastName2',
        admin: true
      },
    }).as('login');
    cy.visit('/login');
    cy.get('input[formControlName=email]').type("yoga@studio.com");
    cy.get('input[formControlName=password]').type("test!1234");
    cy.get('button[type=submit]').click();
    cy.wait('@login');
  };

  const interceptSessions = (sessions) => {
    cy.intercept('GET', '/api/session', {
      body: sessions
    });
  };

  const interceptTeachers = (teachers) => {
    cy.intercept('GET', '/api/teacher', {
      body: teachers
    });
  };


  const sessions = [{
    id: 1,
    name: "session1",
    date: "2024-04-05T08:00:00.000+00:00",
    teacher_id: 1,
    description: "session 1",
    users: [],
    createdAt: "2024-03-15T08:23:02",
    updatedAt: "2024-03-15T08:23:02"
  },
    {
      id: 2,
      name: "session2",
      date: "2024-04-05T08:00:00.000+00:00",
      teacher_id: 1,
      description: "session 2",
      users: [],
      createdAt: "2024-03-15T08:23:02",
      updatedAt: "2024-03-15T08:23:02"
    }];

  const teachers = [{
    id: 1,
    lastName: 'teacher1LastName',
    firstName: 'teacher1FirstName',
    createdAt: new Date(),
    updatedAt: new Date()
  },
    {
      id: 2,
      lastName: 'teacher2LastName',
      firstName: 'teacher2FirstName',
      createdAt: new Date(),
      updatedAt: new Date()
    }];

  it('should create a session (admin)', () => {
    loginAsAdmin();
    interceptSessions(sessions);
    interceptTeachers(teachers);

    cy.contains('Create').click();
    cy.contains('Create session');
    cy.get('button[type="submit"]').should('be.disabled');

    cy.get('input[formControlName="name"]').type("my yoga session");
    cy.get('input[formControlName="date"]').type("2024-03-18");
    cy.get('mat-select[formControlName="teacher_id"]').click().get('mat-option').contains('teacher1FirstName').click();
    cy.get('textarea[formControlName="description"]').type("this is a new yoga session");

    cy.get('button[type="submit"]').should('not.be.disabled');

    const newSession = {
      id: 3,
      name: "my yoga session",
      date: "2024-03-18T08:00:00.000+00:00",
      teacher_id: 1,
      description: "this is a new yoga session",
      users: [],
      createdAt: new Date(),
      updatedAt: new Date()
    };

    cy.intercept('POST', '/api/session', {
      body: newSession
    }).as('createSession');

    cy.intercept('GET', '/api/session', {
      body: [...sessions, newSession]
    }).as('refreshSessions');

    cy.contains('Save').click();

    cy.wait('@createSession');
    cy.wait('@refreshSessions');
    cy.url().should('include', 'sessions');
    cy.contains('this is a new yoga session');
  });

  it('should update a session (admin)', () => {
    const session1 = sessions[0];
    const teacher1 = teachers[0];

    interceptSessions(sessions);
    cy.intercept('GET', '/api/session/1', { body: session1 }).as('getSessionDetail');
    cy.intercept('GET', '/api/teacher/1', { body: teacher1 });
    interceptTeachers(teachers);

    loginAsAdmin();
    // Simuler la navigation vers la page de mise à jour en cliquant sur "Edit"
    cy.contains('Edit').click();
    // Vérifier que l'URL contient "/sessions/update/1"
    cy.url().should('include', '/sessions/update/1');
    // Maintenant, la requête GET devrait être déclenchée
    cy.wait('@getSessionDetail');
    cy.contains('Update session');

    // Remplir le formulaire pour mettre à jour la session
    cy.get('input[formControlName="name"]').clear().type("my session 1 updated");
    cy.get('input[formControlName="date"]').clear().type("2024-03-18");
    cy.get('mat-select[formControlName="teacher_id"]').click()
      .get('mat-option').contains('teacher1FirstName').click();
    cy.get('textarea[formControlName="description"]').clear().type("session 1 updated");

    cy.get('button[type="submit"]').should('not.be.disabled');

    const session1updated = {
      id: 1,
      name: "my session 1 updated",
      date: "2024-03-18T08:00:00.000+00:00",
      teacher_id: 1,
      description: "session 1 updated",
      users: [],
      createdAt: "2024-03-15T08:23:02",
      updatedAt: new Date()
    };

    cy.intercept('PUT', '/api/session/1', {
      body: session1updated
    }).as('updateSession');

    cy.intercept('GET', '/api/session', {
      body: [session1updated, sessions[1]]
    }).as('refreshSessions');

    cy.contains('Save').click();
    cy.wait('@updateSession');
    cy.wait('@refreshSessions');
    cy.url().should('include', 'sessions');
    cy.contains('my session 1 updated');
  });

});
