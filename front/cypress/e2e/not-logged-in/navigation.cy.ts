describe('Not logged in Navigation spec', () => {
    it('Should navigate to login page', () => {
        cy.visit('/')

        cy.get('span[routerlink="login"]').click()
        cy.url().should('include', '/login')
    });

    it('Should navigate to register page', () => {
        cy.visit('/')
        
        cy.get('span[routerlink="register"]').click()
        cy.url().should('include', '/register')
    });

    it('Should navigate to 404 page on invalid route', () => {
        cy.visit('/some/invalid/route', { failOnStatusCode: false })
        
        cy.url().should('include', '/404')
        cy.contains('Page not found !').should('be.visible')
    });
});