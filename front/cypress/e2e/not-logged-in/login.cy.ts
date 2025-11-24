describe('Login spec', () => {
  it('Login successfull', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: {
        id:3,
        email:"yoga@studio.com",
        lastName:"nom",
        firstName:"test",
        admin:true,
        createdAt:"2025-11-24T16:23:38",
        updatedAt:"2025-11-24T16:23:38"
      },
    }).as('login');

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []).as('session')

    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

    cy.wait('@login');

    cy.url().should('include', '/sessions')
  })

  it('Login failed', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: {
        message: 'Invalid credentials'
      },
    })

    cy.get('input[formControlName=email]').type("wrong@example.com")
    cy.get('input[formControlName=password]').type(`${"wrongpassword"}{enter}{enter}`)

    cy.url().should('include', '/login')
  })

  it('The form button cant be clicked when inputs are empty', () => {
    cy.visit('/login')

    cy.get('button[type=submit]').should('be.disabled')
  })

  it('The form button can be clicked when inputs are filled', () => {
    cy.visit('/login')
    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type("test!1234")

    cy.get('button[type=submit]').should('not.be.disabled')
  });

  it('The form button cant be clicked when one input is invalid', () => {
    cy.visit('/login')
    cy.get('input[formControlName=email]').type("invalid-email")
    cy.get('input[formControlName=password]').type("short")

    cy.get('button[type=submit]').should('be.disabled')
  });
});