describe('Update Session spec', () => {
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
        ).as('session');

        cy.intercept('GET', '/api/teacher', [
            {
                "id":1,
                "lastName":"DELAHAYE",
                "firstName":"Margot",
                "createdAt":"2025-11-21T12:44:06",
                "updatedAt":"2025-11-21T12:44:06"
            },
            {
                "id":2,
                "lastName":"THIERCELIN",
                "firstName":"H├®l├¿ne",
                "createdAt":"2025-11-21T12:44:06",
                "updatedAt":"2025-11-21T12:44:06"
            }
        ]).as('teachers');

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
                "updatedAt":"2025-11-21T12:55:52"
            }
        ).as('sessionDetails');
        cy.get('input[formControlName=email]').type("yoga@studio.com")
        cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)
        cy.url().should('include', '/sessions')

        
        cy.contains('Test').closest('mat-card').within(() => {
            cy.contains('button', 'Edit').should('be.visible')
        })
        cy.contains('Test').closest('mat-card').within(() => {
            cy.contains('button', 'Edit').scrollIntoView().click()
        })
        cy.url().should('include', '/sessions/update/1')
    });

    it('should show a back button and navigate to sessions page', () => {
        cy.contains('arrow_back').should('be.visible').click()
        cy.url().should('include', '/sessions')
    });

    it('should display update session form with prefilled values and teachers select options', () => {
        cy.contains('Update session').should('be.visible')
        cy.get('input[formControlName=name]').should('be.visible').should('have.value', 'Test')
        cy.get('textarea[formControlName=description]').should('be.visible').should('have.value', 'defzefze')
        cy.get('input[formControlName=date]').should('be.visible').should('have.value', '2026-02-11')
        cy.get('mat-select[formControlName=teacher_id]').should('be.visible').click()
        cy.get('mat-option.mat-selected').should('have.length', 1)
        cy.get('mat-option.mat-selected').contains('Margot DELAHAYE').should('be.visible')
        cy.get('mat-option').should('have.length', 2)
        cy.get('mat-option').eq(1).contains('H├®l├¿ne THIERCELIN').should('be.visible')
        cy.get('button[type=submit]').should('be.visible')
    });

    it('should submit form buttons not clickable when atleast one field is empty', () => {
        cy.get('input[formControlName=name]').clear()
        cy.get('button[type=submit]').should('be.disabled')
    });

    it('should submit form button clickable when form is untouched', () => {
        cy.get('button[type=submit]').should('not.be.disabled')
    });

    it('should submit form button clickable when all fields are filled', () => {
        cy.get('input[formControlName=name]').clear().type('Updated Session')
        cy.get('textarea[formControlName=description]').clear().type('Updated description')
        cy.get('input[formControlName=date]').clear().type('2026-05-20')
        cy.get('mat-select[formControlName=teacher_id]').click()
        cy.get('mat-option').eq(1).click()

        cy.get('button[type=submit]').should('not.be.disabled')
    });

    it('should submit the form and navigate to sessions page', () => {
        cy.get('input[formControlName=name]').clear().type('Updated Session')
        cy.get('textarea[formControlName=description]').clear().type('Updated description')
        cy.get('input[formControlName=date]').clear().type('2026-05-20')
        cy.get('mat-select[formControlName=teacher_id]').click()
        cy.get('mat-option').eq(1).click()

        cy.intercept('PUT', '/api/session/1', {
            "id":1,
            "name":"Updated Session",
            "date":"2026-05-20T00:00:00.000+00:00",
            "teacher_id":2,
            "description":"Updated description",
            "users":[],
            "createdAt":"2025-11-21T12:55:52",
            "updatedAt":"2025-11-25T10:00:00"
        }).as('updateSession')

        cy.get('button[type=submit]').click()
        cy.url().should('include', '/sessions')
    });


});