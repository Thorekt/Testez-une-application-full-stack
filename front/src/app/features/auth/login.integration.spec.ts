import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { LoginComponent } from './components/login/login.component';
import { SessionService } from 'src/app/services/session.service';
import { expect } from '@jest/globals';
import { AppRoutingModule } from 'src/app/app-routing.module';
import { SessionsModule } from '../sessions/sessions.module';

import { Component } from '@angular/core';

@Component({ template: '<router-outlet></router-outlet>' })
class TestHostComponent {}

describe('Login Integration (auth -> redirect)', () => {
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        HttpClientTestingModule,
        SessionsModule,
        AppRoutingModule
      ],
      declarations: [LoginComponent, TestHostComponent],
      providers: [SessionService],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
  });

  it('authenticates user and navigates to /sessions', async () => {
    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;
    const router = TestBed.inject(Router);
    const sessionService = TestBed.inject(SessionService);

    // Given
    const fakeSession = {
      id: 42,
      email: 'test@example.com',
      firstName: 'Test',
      lastName: 'User',
      admin: false
    };
    // When
    fixture.detectChanges();
    component.form.controls['email'].setValue('test@example.com');
    component.form.controls['password'].setValue('secret');

    const navSpy = jest.spyOn(router, 'navigate').mockImplementation(() => Promise.resolve(true));

    component.submit();

    const req = httpMock.expectOne('api/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ email: 'test@example.com', password: 'secret' });
    req.flush(fakeSession);

    await fixture.whenStable();
    fixture.detectChanges();

    // Then
    expect(sessionService.isLogged).toBe(true);
    expect(sessionService.sessionInformation).toBeDefined();
    expect(navSpy).toHaveBeenCalledWith(['/sessions']);
  });

  it('sets onError when login fails', async () => {
    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;
    const router = TestBed.inject(Router);
    const sessionService = TestBed.inject(SessionService);

    // Given
    fixture.detectChanges();
    component.form.controls['email'].setValue('bad@example.com');
    component.form.controls['password'].setValue('wrong');

    const navSpy = jest.spyOn(router, 'navigate').mockImplementation(() => Promise.resolve(true));

    // When
    component.submit();

    const req = httpMock.expectOne('api/auth/login');
    expect(req.request.method).toBe('POST');
    req.flush({ message: 'Unauthorized' }, { status: 401, statusText: 'Unauthorized' });

    await fixture.whenStable();
    fixture.detectChanges();

    // Then
    expect(component.onError).toBe(true);
    expect(sessionService.isLogged).toBe(false);
    expect(navSpy).not.toHaveBeenCalled();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('sessions page renders the header text', () => {
    // Given
    const listFixture = TestBed.createComponent(require('../sessions/components/list/list.component').ListComponent);
    const sessionService = TestBed.inject(SessionService);
    (sessionService as any).sessionInformation = { id: 1, email: 'a@b', firstName: 'X', lastName: 'Y', admin: true };
    // When
    listFixture.detectChanges();
    const reqSessions = httpMock.expectOne('api/session');
    reqSessions.flush([]);
    listFixture.detectChanges();
    // Then
    const el: HTMLElement = listFixture.nativeElement;
    expect(el.textContent).toContain('Rentals available');
  });
});
