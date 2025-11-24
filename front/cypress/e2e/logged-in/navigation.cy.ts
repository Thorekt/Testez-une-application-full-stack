describe('Logged in Navigation spec', () => {
    beforeEach(() => {
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
      cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)
      cy.url().should('include', '/sessions')
    });

    it('should navigate to Account page', () => {
      cy.get('span[routerlink="me"]').click()
      cy.url().should('include', '/me')
    });

    it('should navigate to Sessions page', () => {
      // place navigation to another page first
      cy.get('span[routerlink="me"]').click()
      cy.url().should('include', '/me')

      cy.get('span[routerlink="sessions"]').click()
      cy.url().should('include', '/sessions')
    });

    it('should logout and navigate to home page', () => {
      cy.get('span[class="link"]').contains('Logout').click()
      cy.url().should('include', '/')

      cy.get('span[routerlink="sessions"]').should('not.exist')
      cy.get('span[routerlink="me"]').should('not.exist')

      cy.get('span[routerlink="login"]').should('exist')
      cy.get('span[routerlink="register"]').should('exist')
    });

    it('Should navigate to 404 page on invalid route', () => {
        cy.visit('/some/invalid/route', { failOnStatusCode: false })

        cy.url().should('include', '/404')
        cy.contains('Page not found !').should('be.visible')
    });
});