/// <reference types="cypress" />
describe('Login spec', () => {
  it('Login successfull', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true
      },
    })

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []).as('session')

    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type('test!1234');
    cy.get('button[type=submit]').click();

    cy.url().should('include', '/sessions')
  })

  it('Login failed', () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: { error: 'Unauthorized' }
    }).as('login');

    cy.intercept('GET', '/api/session', []).as('session');

    cy.visit('/login');

    cy.get('input[formControlName=email]').type('wrong@studio.com');
    cy.get('input[formControlName=password]').type('badPassword');
    cy.get('button[type=submit]').click();

    cy.wait('@login');

    cy.contains('An error occurred').should('be.visible');
    cy.url().should('include', '/login');
  });
});
