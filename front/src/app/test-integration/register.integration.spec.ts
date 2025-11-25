import { TestBed } from '@angular/core/testing';
import { Component } from '@angular/core';
import { ComponentFixture } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

import { RegisterComponent } from '../features/auth/components/register/register.component';
import { AuthService } from '../features/auth/services/auth.service';
import { AuthRoutingModule } from '../features/auth/auth-routing.module';

@Component({ template: '<div>login stub</div>' })
class StubLoginComponent {}

describe('RegisterComponent Integration', () => {
  let fixture: ComponentFixture<RegisterComponent>;
  let component: RegisterComponent;
  let router: Router;
  let location: Location;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegisterComponent, StubLoginComponent],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([
          { path: 'login', component: StubLoginComponent }
        ]),
        AuthRoutingModule,
        ReactiveFormsModule,
        BrowserAnimationsModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule,
        MatButtonModule
      ],
      providers: [AuthService]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    location = TestBed.inject(Location);
    httpMock = TestBed.inject(HttpTestingController);

    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('submits the form, registers the user and redirects to /login', async () => {
    // Given
    component.form.setValue({
      email: 'test@example.com',
      password: 'password123',
      firstName: 'Test',
      lastName: 'User'
    });

    // When
    component.submit();
    fixture.detectChanges();

    const req = httpMock.expectOne('api/auth/register');
    req.flush({});

    // Then
    await fixture.whenStable();
    expect(location.path()).toBe('/login');
  });

  it('sets onError to true when register fails', () => {
    // Given
    component.form.setValue({
      email: 'test@example.com',
      password: 'password123',
      firstName: 'Test',
      lastName: 'User'
    });

    // When
    component.submit();
    fixture.detectChanges();

    const req = httpMock.expectOne('api/auth/register');
    req.flush({}, { status: 400, statusText: 'Bad Request' });

    // Then
    fixture.detectChanges();
    expect(component.onError).toBeTruthy();
  });

  it('has the submit button disabled when the form is empty', () => {
    // Given
    component.form.setValue({
      email: '',
      password: '',
      firstName: '',
      lastName: ''
    });
    fixture.detectChanges();

    // When
    const button: HTMLButtonElement = fixture.nativeElement.querySelector('button[type="submit"]');
    
    // Then
    expect(button.disabled).toBeTruthy();
  });
});
