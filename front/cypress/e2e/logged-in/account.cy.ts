describe('Admin Account Page spec', () => {
    beforeEach(() => {
        cy.visit('/login')

        cy.intercept('POST', '/api/auth/login', {
            body: {
                id:3,
                email:"yoga@studio.com",
                lastName:"nom",
                firstName:"test",
                admin:true,
                createdAt:"2025-11-24T16:23:38",
                updatedAt:"2025-11-24T16:23:38"
            },
        })

        cy.intercept(
        {
            method: 'GET',
            url: '/api/session',
        },
        []).as('session')

        cy.intercept('GET', '/api/user/3', {
            id:3,
            email:"yoga@studio.com",
            lastName:"nom",
            firstName:"test",
            admin:true,
            createdAt:"2025-11-24T16:23:38",
            updatedAt:"2026-12-25T16:23:38"
        })

        cy.get('input[formControlName=email]').type("yoga@studio.com")
        cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)
        cy.url().should('include', '/sessions')

        cy.get('span[routerlink="me"]').click()
        cy.url().should('include', '/me')
    });

    it('should display user information', () => {
        cy.contains('test NOM').should('be.visible')
        cy.contains('yoga@studio.com').should('be.visible')

        cy.contains('You are admin').should('be.visible')        
        cy.contains('Delete my account:').should('not.exist')
        cy.contains('Detail').should('not.exist')

        cy.contains('Create at: November 24, 2025').should('be.visible')
        cy.contains('Last update: December 25, 2026').should('be.visible')
    }); 

    it('should show a back button and navigate to sessions page', () => {
        cy.contains('arrow_back').should('be.visible').click()
        cy.url().should('include', '/sessions')
    });
});

describe('Regular User Account Page spec', () => {
    beforeEach(() => {
        cy.visit('/login')

        cy.intercept('POST', '/api/auth/login', {
            body: {
                id:4,
                email:"user@studio.com",
                lastName:"userlast",
                firstName:"userfirst",
                admin:false,
                createdAt:"2025-10-20T10:15:30",
                updatedAt:"2026-11-22T12:20:25"
            },
        })

        cy.intercept(
        {
            method: 'GET',
            url: '/api/session',
        },
        []).as('session')

        cy.intercept('GET', '/api/user/4', {
            id:4,
            email:"user@studio.com",
            lastName:"userlast",
            firstName:"userfirst",
            admin:false,
            createdAt:"2025-10-20T10:15:30",
            updatedAt:"2026-11-22T12:20:25"
        })

        cy.get('input[formControlName=email]').type("user@studio.com")
        cy.get('input[formControlName=password]').type(`${"userpass!1234"}{enter}{enter}`)
        cy.url().should('include', '/sessions')

        cy.get('span[routerlink="me"]').click()
        cy.url().should('include', '/me')
    });

    it('should display user information', () => {
        cy.contains('userfirst USERLAST').should('be.visible')
        cy.contains('user@studio.com').should('be.visible')
        cy.contains('You are admin').should('not.exist')
        cy.contains('Delete my account:').should('be.visible')
        cy.contains('Detail').should('be.visible')

        cy.contains('Create at: October 20, 2025').should('be.visible')
        cy.contains('Last update: November 22, 2026').should('be.visible')
    });

    it('should show a back button and navigate to sessions page', () => {
        cy.contains('arrow_back').should('be.visible').click()
        cy.url().should('include', '/sessions')
    });

    it('should delete the user account', () => {
        cy.intercept('DELETE', '/api/user/4', {
            body:{
                message: "User deleted successfully!"
            }
        })

        cy.contains('Delete my account:').should('be.visible')
        
        cy.contains('Detail').should('be.visible').click()

        cy.get('span[routerlink="sessions"]').should('not.exist')
        cy.get('span[routerlink="me"]').should('not.exist')

        cy.get('span[routerlink="login"]').should('exist')
        cy.get('span[routerlink="register"]').should('exist')
    });
});