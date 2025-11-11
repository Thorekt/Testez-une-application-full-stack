import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';

import { RegisterComponent } from './register.component';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      imports: [
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,  
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit redirect after register success', () => {
    // Given
    const registerRequest = {
      email: 'test@example.com',
      password: 'password123',
      firstName: 'Test',
      lastName: 'User'
    };

    component.form.setValue(registerRequest);

    const registerSpy = jest.spyOn(component['authService'], 'register').mockImplementation(() => {
      return {
        subscribe: (obj: any) => {
          obj.next({});
        }
      } as any;
    });

    const routerNavigateSpy = jest.spyOn(component['router'], 'navigate').mockImplementation(() => Promise.resolve(true));

    // When
    component.submit();

    // Then
    expect(registerSpy).toHaveBeenCalledWith(registerRequest);
    expect(routerNavigateSpy).toHaveBeenCalledWith(['/login']);
  });

  it('should set onError to true on register error', () => {
    // Given
    const registerRequest = {
      email: 'test@example.com',
      password: 'password123',
      firstName: 'Test',
      lastName: 'User'
    };
    component.form.setValue(registerRequest);

    const registerSpy = jest.spyOn(component['authService'], 'register').mockImplementation(() => {
      return {
        subscribe: (obj: any) => {
          obj.error({});
        }
      } as any;
    });

    // When
    component.submit();

    // Then
    expect(registerSpy).toHaveBeenCalledWith(registerRequest);
    expect(component.onError).toBe(true);
  });
});
