describe('Admin user Sessions spec with multiples sessions in list', () => {
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
            [
                {
                    "id":1,
                    "name":"Test",
                    "date":"2026-02-11T00:00:00.000+00:00",
                    "teacher_id":1,
                    "description":"defzefze",
                    "users":[],
                    "createdAt":"2025-11-21T12:55:52",
                    "updatedAt":"2025-11-21T12:55:52"
                },
                {
                    "id":2,
                    "name":"Another Session",
                    "date":"2026-03-15T00:00:00.000+00:00",
                    "teacher_id":2,
                    "description":"Another description",
                    "users":[],
                    "createdAt":"2025-11-22T10:30:00",
                    "updatedAt":"2025-11-22T10:30:00"
                }
            ]
        ).as('session')

        cy.get('input[formControlName=email]').type("yoga@studio.com")
        cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)
        cy.url().should('include', '/sessions')
    });

    it('should display sessions list', () => {        
        cy.contains('Rentals available').should('be.visible')

        cy.contains('Test').should('be.visible')
        cy.contains('February 11, 2026').should('be.visible')
        cy.contains('defzefze').should('be.visible')
        cy.contains('Test').closest('mat-card').within(() => {
            cy.contains('button', 'Detail').should('be.visible')
            cy.contains('button', 'Edit').should('be.visible')
        })

        cy.contains('Another Session').should('be.visible')
        cy.contains('March 15, 2026').should('be.visible')
        cy.contains('Another description').should('be.visible')
        cy.contains('Another Session').closest('mat-card').within(() => {
            cy.contains('button', 'Detail').should('be.visible')
            cy.contains('button', 'Edit').should('be.visible')
        })
    });

    it('should display create session button and navigate to create session page', () => {
        cy.contains('Create').should('be.visible').click()
        cy.url().should('include', '/sessions/create')
    });


    it('should navigate to the right session detail page', () => {
        cy.contains('Test').closest('mat-card').within(() => {
            cy.contains('button', 'Detail').scrollIntoView().click()
        })
        cy.url().should('include', '/sessions/detail/1')

        cy.contains('Sessions').click()
        cy.url().should('include', '/sessions')

        cy.contains('Another Session').closest('mat-card').within(() => {
            cy.contains('button', 'Detail').scrollIntoView().click()
        })
        cy.url().should('include', '/sessions/detail/2')
    });

    it('should show a edit session button for each session and navigate to edit session page', () => {
        cy.contains('Test').closest('mat-card').within(() => {
            cy.contains('button', 'Edit').should('be.visible').scrollIntoView().click()
        })
        cy.url().should('include', '/sessions/update/1')

        cy.contains('Sessions').click()
        cy.url().should('include', '/sessions')

        cy.contains('Another Session').closest('mat-card').within(() => {
            cy.contains('button', 'Edit').should('be.visible').scrollIntoView().click()
        })
        cy.url().should('include', '/sessions/update/2')
    });
});

describe('Admin user Sessions spec with empty sessions list', () => {
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
            []
        ).as('session')

        cy.get('input[formControlName=email]').type("yoga@studio.com")
        cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)
        cy.url().should('include', '/sessions')
    });

    it('should display no sessions', () => {
        cy.contains('Rentals available').should('be.visible')
        cy.get('button').contains('Detail').should('not.exist')
        cy.get('button').contains('Edit').should('not.exist')
    });

    it('should display create session button and navigate to create session page', () => {
        cy.contains('Create').should('be.visible').click()
        cy.url().should('include', '/sessions/create')
    });
});

describe('Regular user Sessions spec with multiples sessions in list', () => {
    beforeEach(() => {
        cy.visit('/login')

        cy.intercept('POST', '/api/auth/login', {
            body: {
                id:4,
                email:"user@yoga.com",
                lastName:"userlast",
                firstName:"userfirst",
                admin:false,
                createdAt:"2025-11-24T16:23:38",
                updatedAt:"2025-11-24T16:23:38"
            },
        })

        cy.intercept(
            {
                method: 'GET',
                url: '/api/session',
            },
            [
                {
                    "id":1,
                    "name":"Test",
                    "date":"2026-02-11T00:00:00.000+00:00",
                    "teacher_id":1,
                    "description":"defzefze",
                    "users":[],
                    "createdAt":"2025-11-21T12:55:52",
                    "updatedAt":"2025-11-21T12:55:52"
                },
                {
                    "id":2,
                    "name":"Another Session",
                    "date":"2026-03-15T00:00:00.000+00:00",
                    "teacher_id":2,
                    "description":"Another description",
                    "users":[],
                    "createdAt":"2025-11-22T10:30:00",
                    "updatedAt":"2025-11-22T10:30:00"
                }
            ]
        ).as('session')

        cy.get('input[formControlName=email]').type("user@yoga.com")
        cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)
        cy.url().should('include', '/sessions')
    });

    it('should display sessions list without edit button', () => {        
        cy.contains('Rentals available').should('be.visible')

        cy.contains('Test').should('be.visible')
        cy.contains('February 11, 2026').should('be.visible')
        cy.contains('defzefze').should('be.visible')
        cy.contains('Test').closest('mat-card').within(() => {
            cy.get('button').contains('Detail').should('be.visible')
            cy.get('button').contains('Edit').should('not.exist')
        })

        cy.contains('Another Session').should('be.visible')
        cy.contains('March 15, 2026').should('be.visible')
        cy.contains('Another description').should('be.visible')
        cy.contains('Another Session').closest('mat-card').within(() => {
            cy.get('button').contains('Detail').should('be.visible')
            cy.get('button').contains('Edit').should('not.exist')
        })
    });
    
    it('should not display create session button', () => {
        cy.contains('Create').should('not.exist')
    });

    it('should navigate to the right session detail page', () => {        
        cy.contains('Test').closest('mat-card').within(() => {
            cy.get('button').contains('Detail').click()
        })
        cy.url().should('include', '/sessions/detail/1')
        
        cy.contains('Sessions').click()
        cy.url().should('include', '/sessions')

        cy.contains('Another Session').closest('mat-card').within(() => {
            cy.get('button').contains('Detail').click()
        })
        cy.url().should('include', '/sessions/detail/2')
    });
});

describe('Regular user Sessions spec with empty sessions list', () => {
    beforeEach(() => {
        cy.visit('/login')
        
        cy.intercept('POST', '/api/auth/login', {
            body: {
                id:4,
                email:"user@yoga.com",
                lastName:"userlast",
                firstName:"userfirst",
                admin:false,
                createdAt:"2025-11-24T16:23:38",
                updatedAt:"2025-11-24T16:23:38"
            },
        })

        cy.intercept(
            {
                method: 'GET',
                url: '/api/session',
            },
            []
        ).as('session')

        cy.get('input[formControlName=email]').type("user@yoga.com")
        cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)
        cy.url().should('include', '/sessions')
    });

    it('should display no sessions', () => {
        cy.contains('Rentals available').should('be.visible')
        cy.contains('Detail').should('not.exist')
        cy.contains('Edit').should('not.exist')
    });

    it('should not display create session button', () => {
        cy.contains('Create').should('not.exist')
    });
});