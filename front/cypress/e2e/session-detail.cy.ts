/// <reference types="cypress" />

describe('Detail tests', () => {
  const teacher1 = {
    id: 1,
    lastName: "teacher1LastName",
    firstName: "teacher1FirstName",
    createdAt: new Date(),
    updatedAt: new Date()
  };

  const session1 = {
    id: 1,
    name: "session1",
    date: "2024-04-05T08:00:00.000+00:00",
    teacher_id: 1,
    description: "session 1",
    users: [],
    createdAt: "2024-03-15T08:23:02",
    updatedAt: "2024-03-15T08:23:02"
  };

  const session2 = {
    id: 2,
    name: "session2",
    date: "2024-04-05T08:00:00.000+00:00",
    teacher_id: 1,
    description: "session 2",
    users: [],
    createdAt: "2024-03-15T08:23:02",
    updatedAt: "2024-03-15T08:23:02"
  };

  const sessions = [session1, session2];

  const login = (username, password, isAdmin) => {
    cy.visit('/login');
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: isAdmin ? 1 : 2,
        username,
        firstName: 'firstName2',
        lastName: 'lastName2',
        admin: isAdmin
      },
    });
    cy.get('input[formControlName=email]').type(username);
    cy.get('input[formControlName=password]').type(`${password}{enter}{enter}`);
  };

  beforeEach(() => {
    cy.intercept('GET', '/api/session', {
      body: sessions
    }).as("getsession");
  });

  it('should display the details of a session for user', () => {
    login('user1@email.com', 'password', false);

    cy.intercept('GET', '/api/session/1', {
      body: session1
    }).as("getSession1");

    cy.intercept('GET', '/api/teacher/1', {
      body: teacher1
    }).as("getTeacher1");

    cy.contains('Detail').click();

    cy.wait('@getSession1');
    cy.wait('@getTeacher1');

    cy.url().should('include', 'sessions/detail/1');
    cy.contains('attendees');
    cy.contains('Session1');
    cy.contains('Delete').should('not.exist');
    cy.contains('Participate').should('be.visible');
  });

  it('should display the details of a session for admin', () => {
    login('yoga@studio.com', 'test!1234', true);

    cy.intercept('GET', '/api/session/1', {
      body: session1
    }).as("getSession1");

    cy.intercept('GET', '/api/teacher/1', {
      body: teacher1
    }).as("getTeacher1");

    cy.contains('Detail').click();

    cy.wait('@getSession1');
    cy.wait('@getTeacher1');

    cy.url().should('include', 'sessions/detail/1');
    cy.contains('attendees');
    cy.contains('Session1');
    cy.contains('Delete').should('be.visible');
    cy.contains('Participate').should('not.exist');
    cy.contains('Do not participate').should('not.exist');
  });


  it('should navigate back', () => {

    cy.url().should('include', '/sessions/detail/1')
    // Vérifier que les données de l'utilisateur sont affichées correctement
    cy.get('h1').should('contain', 'Session1');

    // Simuler un clic sur le bouton qui appelle la méthode back()
    cy.get('[fxlayout="row"] > .mat-focus-indicator').click();

    // Vérifier que l'URL de la page a changé pour revenir à la page précédente
    cy.url().should('not.include', '/sessions/detail/1');
  });

  it('should delete a session and display a confirmation message for admin', () => {
    login('yoga@studio.com', 'test!1234', true);

    cy.intercept('GET', '/api/session/1', {
      body: session1
    }).as("getSession1");

    cy.intercept('GET', '/api/teacher/1', {
      body: teacher1
    }).as("getTeacher1");

    cy.contains('Detail').click();

    cy.wait('@getSession1');
    cy.wait('@getTeacher1');

    cy.url().should('include', 'sessions/detail/1');
    cy.contains('Delete').should('be.visible');

    cy.intercept('DELETE', `/api/session/${session1.id}`, {
      statusCode: 200
    }).as("deleteSession");

    cy.contains('Delete').click();

    cy.wait('@deleteSession');

    cy.get('.mat-snack-bar-container').should('contain', 'Session deleted !');
    cy.url().should('include', 'sessions');
  });
});
