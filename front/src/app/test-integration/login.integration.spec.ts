import { TestBed } from '@angular/core/testing';
import { Component } from '@angular/core';
import { ComponentFixture } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

import { LoginComponent } from '../features/auth/components/login/login.component';
import { SessionService } from '../services/session.service';
import { AuthService } from '../features/auth/services/auth.service';

@Component({ template: '<div>sessions stub</div>' })
class StubSessionsComponent {}

describe('LoginComponent Integration', () => {
  let fixture: ComponentFixture<LoginComponent>;
  let component: LoginComponent;
  let router: Router;
  let httpMock: HttpTestingController;
  let sessionService: SessionService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginComponent, StubSessionsComponent],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([
          { path: 'sessions', component: StubSessionsComponent }
        ]),
        ReactiveFormsModule,
        BrowserAnimationsModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule,
        MatButtonModule
      ],
      providers: [SessionService, AuthService]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;

    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate').mockResolvedValue(true); // <<< FINI les warnings

    httpMock = TestBed.inject(HttpTestingController);
    sessionService = TestBed.inject(SessionService);

    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('submits the form, logs in the user and redirects', async () => {
    // Given
    component.form.setValue({
      email: 'test@example.com',
      password: 'password123'
    });

    // When
    component.submit();
    fixture.detectChanges();

    const req = httpMock.expectOne('api/auth/login');
    req.flush({ id: 1, email: 'test@example.com' });

    // Then
    expect(sessionService.isLogged).toBeTruthy();
    expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
  });

  it('sets onError to true when login fails', () => {
    // Given
    component.form.setValue({
      email: 'test@example.com',
      password: 'password123'
    });

    // When
    component.submit();
    fixture.detectChanges();

    const req = httpMock.expectOne('api/auth/login');
    req.flush({ message: 'error' }, { status: 401, statusText: 'Unauthorized' });

    // Then
    expect(component.onError).toBeTruthy();
  });

  it('has the submit button disabled when the form is empty', () => {
    // Given
    component.form.reset();
    fixture.detectChanges();

    // When
    const button: HTMLButtonElement = fixture.nativeElement.querySelector('button[type="submit"]');

    // Then
    expect(button.disabled).toBe(true);
  });
});
