describe('Create Session spec', () => {
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
        ]).as('teachers')

        cy.get('input[formControlName=email]').type("yoga@studio.com")
        cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)
        cy.url().should('include', '/sessions')

        cy.contains('Create').should('be.visible').click()
        cy.url().should('include', '/sessions/create')
    });

    it('should display create session form', () => {
        cy.contains('Create session').should('be.visible')
        cy.get('input[formControlName=name]').should('be.visible')
        cy.get('textarea[formControlName=description]').should('be.visible')
        cy.get('input[formControlName=date]').should('be.visible')
        cy.get('mat-select[formControlName=teacher_id]').should('be.visible')
        cy.get('button[type=submit]').should('be.visible')
    });

    it('should show multiple teachers in select options', () => {
        cy.get('mat-select[formControlName=teacher_id]').click()
        cy.get('mat-option').should('have.length', 2)
        cy.get('mat-option').eq(0).contains('Margot DELAHAYE').should('be.visible')
        cy.get('mat-option').eq(1).contains('H├®l├¿ne THIERCELIN').should('be.visible')
    });

    it('should submit form button not clickable when inputs are empty', () => {
        cy.get('button[type=submit]').should('be.disabled')
    });

    it('should submit form button clickable when inputs are filled', () => {
        cy.get('input[formControlName=name]').type('New Session')
        cy.get('textarea[formControlName=description]').type('This is a new session description.')
        cy.get('input[formControlName=date]').type('2026-04-20')
        cy.get('mat-select[formControlName=teacher_id]').click()
        cy.get('mat-option').eq(0).click()

        cy.get('button[type=submit]').should('not.be.disabled')
    });

    it('should submit form button not clickable when one input is empty', () => {
        cy.get('input[formControlName=name]').type('New Session')
        cy.get('textarea[formControlName=description]').type('This is a new session description.')
        
        cy.get('mat-select[formControlName=teacher_id]').click()
        cy.get('mat-option').eq(0).click()

        cy.get('button[type=submit]').should('be.disabled')
    });

    it('should submit the form and navigate to sessions page', () => {
        cy.intercept('POST', '/api/session', {
            body: {
                name:"NomDeSession1",
                date:"2025-11-22",
                teacher_id:1,
                description:"zaezeazeaze"
            },
        }).as('createSession')

        cy.get('input[formControlName=name]').type('NomDeSession1')
        cy.get('textarea[formControlName=description]').type('zaezeazeaze')
        cy.get('input[formControlName=date]').type('2025-11-22')
        cy.get('mat-select[formControlName=teacher_id]').click()
        cy.get('mat-option').eq(0).click()

        cy.get('button[type=submit]').click()

        cy.wait('@createSession').its('request.body').should('deep.equal', {
            name:"NomDeSession1",
            date:"2025-11-22",
            teacher_id:1,
            description:"zaezeazeaze"
        })
        
        cy.url().should('include', '/sessions')
    });

    it('should show a back button and navigate to sessions page', () => {
        cy.contains('arrow_back').should('be.visible').click()
        cy.url().should('include', '/sessions')
    });
});
