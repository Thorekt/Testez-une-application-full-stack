import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';

import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [SessionService],
      imports: [
        RouterTestingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule]
    })
      .compileComponents();
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit log the session in on successful login and redirect', () => {
    // Given
    const loginRequest = {
      email: 'test@example.com',
      password: 'password123'
    };

    component.form.setValue(loginRequest);


    const loginSpy = jest.spyOn(component['authService'], 'login').mockImplementation(() => {
      return {
        subscribe: (obj: any) => {
          obj.next({});
        }
      } as any;
    });
    const sessionServiceLoginSpy = jest.spyOn(component['sessionService'], 'logIn').mockImplementation(() => {});
    const routerNavigateSpy = jest.spyOn(component['router'], 'navigate').mockImplementation(() => Promise.resolve(true));

    
    // When
    component.submit();

    // Then
    expect(loginSpy).toHaveBeenCalled();
    expect(sessionServiceLoginSpy).toHaveBeenCalled();
    expect(routerNavigateSpy).toHaveBeenCalledWith(['/sessions']);

  });

  it('should set onError to true on failed login', () => {
    // Given
    const loginRequest = {
      email: 'test@example.com',
      password: 'password123'
    };

    component.form.setValue(loginRequest);

    const loginSpy = jest.spyOn(component['authService'], 'login').mockImplementation(() => {
      return {
        subscribe: (obj: any) => {
          obj.error(new Error('Login failed'));
        }
      } as any;
    });

    // When
    component.submit();

    // Then
    expect(loginSpy).toHaveBeenCalled();
    expect(component.onError).toBe(true);
  });
});
