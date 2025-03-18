/// <reference types="cypress" />
describe('Me component', () => {
  beforeEach(() => {

    cy.visit('/login');
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true
      },
    });
    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []
    ).as('session');

    cy.get('input[formControlName=email]').type("yoga@studio.com");
    cy.get('input[formControlName=password]').type("test!1234{enter}{enter}");

    cy.url().should('include', '/sessions');
  });

  it('should display simple user data', () => {
    cy.intercept('GET', '/api/user/1', {
      body: {
        id: 1,
        email: 'toto@toto.com',
        lastName: 'lastName',
        firstName: 'firstName',
        admin: false,
        createdAt: '2023-03-14',
        updatedAt: '2023-03-14',
      },
    });
    // Utiliser un sélecteur data-cy serait idéal ici
    cy.get('[routerlink="me"]').click();

    cy.url().should('include', '/me');
    cy.get('mat-card-title h1').should('contain', 'User information');
    cy.get('.mat-card-content > [fxlayoutalign="start center"] > :nth-child(1)')
      .should('contain', 'Name: firstName LASTNAME');
    cy.get('.mat-card-content > [fxlayoutalign="start center"] > :nth-child(2)')
      .should('contain', 'Email: toto@toto.com');
    cy.get('.my2 > p').should('contain', 'Delete my account');
    cy.get('.ml1').should('be.visible');
    cy.get('.p2 > :nth-child(1)').should('contain', 'March 14, 2023');
    cy.get('.p2 > :nth-child(2)').should('contain', 'March 14, 2023');
  });

  it('should display admin user data', () => {
    cy.intercept('GET', '/api/user/1', {
      body: {
        id: 1,
        email: 'toto@toto.com',
        lastName: 'lastName',
        firstName: 'firstName',
        admin: true,
        createdAt: '2023-03-14',
        updatedAt: '2023-03-14',
      },
    });
    cy.get('[routerlink="me"]').click();

    cy.url().should('include', '/me');
    cy.get('mat-card-title h1').should('contain', 'User information');
    cy.get('.mat-card-content > [fxlayoutalign="start center"] > :nth-child(1)')
      .should('contain', 'Name: firstName LASTNAME');
    cy.get('.mat-card-content > [fxlayoutalign="start center"] > :nth-child(2)')
      .should('contain', 'Email: toto@toto.com');
    cy.get('.my2').should('contain', 'You are admin');
    cy.get('.p2 > :nth-child(1)').should('contain', 'March 14, 2023');
    cy.get('.p2 > :nth-child(2)').should('contain', 'March 14, 2023');
  });

  it('should navigate back', () => {
    cy.intercept('GET', '/api/user/1', {
      body: {
        id: 1,
        email: 'toto@toto.com',
        lastName: 'lastName',
        firstName: 'firstName',
        admin: true,
        createdAt: '2023-03-14',
        updatedAt: '2023-03-14',
      },
    });
    cy.get('[routerlink="me"]').click();

    cy.url().should('include', '/me');
    cy.get('mat-card-title h1').should('contain', 'User information');
    cy.get('button[mat-icon-button]').click();
    cy.url().should('not.include', '/me');
  });

  it('should delete user account', () => {
    cy.intercept('GET', '/api/user/1', {
      body: {
        id: 1,
        email: 'toto@toto.com',
        lastName: 'lastName',
        firstName: 'firstName',
        admin: false,
        createdAt: '2023-03-14',
        updatedAt: '2023-03-14',
      },
    });
    cy.get('[routerlink="me"]').click();

    cy.url().should('include', '/me');
    cy.intercept('DELETE', '/api/user/*', {
      statusCode: 204,
      body: {},
    }).as('deleteUser');

    cy.get('button[color="warn"]').click();

    cy.wait('@deleteUser').its('request.url').should('match', /\/api\/user\/\d+$/);
    cy.get('snack-bar-container').should('contain', 'Your account has been deleted !');
    cy.url().should('include', '/');
  });
});
