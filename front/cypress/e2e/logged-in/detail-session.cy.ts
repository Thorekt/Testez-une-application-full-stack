describe('Admin user Detail Session spec', () => {
    beforeEach(() => {
        cy.visit('/login');

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
        });

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
                    "updatedAt":"2026-12-22T12:55:52"
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
        ).as('session');

        cy.intercept(
            {
                method: 'GET',
                url: '/api/session/1',
            },
            {
                "id":1,
                "name":"Test",
                "date":"2026-02-11T00:00:00.000+00:00",
                "teacher_id":1,
                "description":"defzefze",
                "users":[],
                "createdAt":"2025-11-21T12:55:52",
                "updatedAt":"2026-12-22T12:55:52"
            }
        ).as('sessionDetails');

        cy.intercept('GET', '/api/teacher/1', 
            {
                "id":1,
                "lastName":"DELAHAYE",
                "firstName":"Margot",
                "createdAt":"2025-11-21T12:44:06",
                "updatedAt":"2025-11-21T12:44:06"
            }
        ).as('teacherDetails');
        
        cy.get('input[formControlName=email]').type("yoga@studio.com")
        cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)
        cy.url().should('include', '/sessions')

        
        cy.contains('Test').closest('mat-card').within(() => {
            cy.contains('button', 'Detail').should('be.visible')
        })

        cy.contains('Test').closest('mat-card').within(() => {
            cy.get('button').contains('Detail').click()
        })
        cy.url().should('include', '/sessions/detail/1')
    });

    it('should show a back button and navigate to sessions page', () => {
        cy.contains('arrow_back').should('be.visible').click()
        cy.url().should('include', '/sessions')
    });

    it('should display session details', () => {
        cy.contains('Test').should('be.visible')
        cy.contains('February 11, 2026').should('be.visible')
        cy.contains('defzefze').should('be.visible')
        cy.contains('Margot DELAHAYE').should('be.visible')
        cy.contains('0 attendees').should('be.visible')
        cy.contains('Create at: November 21, 2025').should('be.visible')
        cy.contains('Last update: December 22, 2026').should('be.visible')
    });

    it('should display delete button for admin user and delete session', () => {
        cy.contains('button', 'Delete').should('be.visible').click()

        cy.intercept('DELETE', '/api/session/1', {}).as('deleteSession');
    
        cy.url().should('include', '/sessions')
    })
});

describe('Regular user Detail Session spec', () => {
    beforeEach(() => {
        cy.visit('/login');

        cy.intercept('POST', '/api/auth/login', {
            body: {
                id:3,
                email:"yoga@studio.com",
                lastName:"nom",
                firstName:"test",
                admin:false,
                createdAt:"2025-11-24T16:23:38",
                updatedAt:"2025-11-24T16:23:38"
            },
        });

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
                    "updatedAt":"2026-12-22T12:55:52"
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
        ).as('session');

        cy.intercept(
            {
                method: 'GET',
                url: '/api/session/1',
            },
            {
                "id":1,
                "name":"Test",
                "date":"2026-02-11T00:00:00.000+00:00",
                "teacher_id":1,
                "description":"defzefze",
                "users":[],
                "createdAt":"2025-11-21T12:55:52",
                "updatedAt":"2026-12-22T12:55:52"
            }
        ).as('sessionDetails');

        cy.intercept('GET', '/api/teacher/1', 
            {
                "id":1,
                "lastName":"DELAHAYE",
                "firstName":"Margot",
                "createdAt":"2025-11-21T12:44:06",
                "updatedAt":"2025-11-21T12:44:06"
            }
        ).as('teacherDetails');
        
        cy.get('input[formControlName=email]').type("yoga@studio.com")
        cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)
        cy.url().should('include', '/sessions')

        
        cy.contains('Test').closest('mat-card').within(() => {
            cy.contains('button', 'Detail').should('be.visible')
        })

        cy.contains('Test').closest('mat-card').within(() => {
            cy.get('button').contains('Detail').click()
        })
        cy.url().should('include', '/sessions/detail/1')
    });

    it('should not display delete button for regular user', () => {
        cy.contains('button', 'Delete').should('not.exist')
    });

     it('should show a back button and navigate to sessions page', () => {
        cy.contains('arrow_back').should('be.visible').click()
        cy.url().should('include', '/sessions')
    });

    it('should display session details', () => {
        cy.contains('Test').should('be.visible')
        cy.contains('February 11, 2026').should('be.visible')
        cy.contains('defzefze').should('be.visible')
        cy.contains('Margot DELAHAYE').should('be.visible')
        cy.contains('0 attendees').should('be.visible')
        cy.contains('Create at: November 21, 2025').should('be.visible')
        cy.contains('Last update: December 22, 2026').should('be.visible')
    });
});