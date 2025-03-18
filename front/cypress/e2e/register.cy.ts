/// <reference types="cypress" />
describe('Register spec', () => {
  beforeEach(() => {
    cy.visit('/register');
  });

  it('Register successful', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 200,
    }).as('register');


    cy.get('input[formControlName="firstName"]').type('John');
    cy.get('input[formControlName="lastName"]').type('Doe');
    cy.get('input[formControlName="email"]').type('john.doe@example.com');
    cy.get('input[formControlName="password"]').type('password123');
    cy.get('button[type=submit]').click();

    cy.wait('@register');

    cy.url().should('include', '/login');
  });

  it('Register failed', () => {

    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400,
      body: { error: 'Registration failed' }
    }).as('register');



    cy.get('input[formControlName="firstName"]').type('John');
    cy.get('input[formControlName="lastName"]').type('Doe');
    cy.get('input[formControlName="email"]').type('john.doe@example.com');
    cy.get('input[formControlName="password"]').type('password123');


    cy.get('button[type=submit]').click();

    cy.wait('@register');

    cy.contains('An error occurred').should('be.visible');
    cy.url().should('include', '/register');
  });
});
