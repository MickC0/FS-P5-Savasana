/// <reference types="cypress" />

describe('Not Found Component', () => {
  it('should display "Page not found !" when navigating to an invalid route', () => {
    cy.visit('/route-inexistante', { failOnStatusCode: false });

    cy.contains('Page not found !').should('be.visible');
  });
});
