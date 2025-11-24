describe('Register spec', () => {
    it('Register successfull', () => {
        cy.visit('/register')

        cy.intercept('POST', '/api/auth/register', {
            body:{
                message: "User registered successfully!"
            }
        })

        cy.get('input[formcontrolname="firstName"]').type('John')
        cy.get('input[formcontrolname="lastName"]').type('Doe')
        cy.get('input[formcontrolname="email"]').type('john.doe@example.com')
        cy.get('input[formcontrolname="password"]').type('password123')

        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/login')
    })

    it('Register failed due to existing email', () => {
        cy.visit('/register')
        
        cy.intercept('POST', '/api/auth/register', {
            statusCode: 400,
            body:{
                message: "Email already in use"
            }
        })

        cy.get('input[formcontrolname="firstName"]').type('Jane')
        cy.get('input[formcontrolname="lastName"]').type('Doe')
        cy.get('input[formcontrolname="email"]').type('jane.doe@example.com')
        cy.get('input[formcontrolname="password"]').type('password123')

        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/register')
        cy.contains('An error occurred').should('be.visible')
    })

    it('The form button cant be clicked when inputs are empty', () => {
        cy.visit('/register')

        cy.get('button[type="submit"]').should('be.disabled')
    });

    it('The form button can be clicked when inputs are filled', () => {
        cy.visit('/register')
        cy.get('input[formcontrolname="firstName"]').type('Alice')
        cy.get('input[formcontrolname="lastName"]').type('Smith')
        cy.get('input[formcontrolname="email"]').type('alice.smith@example.com')
        cy.get('input[formcontrolname="password"]').type('password123')

        cy.get('button[type="submit"]').should('not.be.disabled')
    }); 

    it('The form button cant be clicked when one input is invalid', () => {

        cy.visit('/register')
        cy.get('input[formcontrolname="firstName"]').type('Bob')
        cy.get('input[formcontrolname="lastName"]').type('Brown')
        cy.get('input[formcontrolname="email"]').type('invalid-email')
        cy.get('input[formcontrolname="password"]').type('short')

        cy.get('button[type="submit"]').should('be.disabled')
    });
});