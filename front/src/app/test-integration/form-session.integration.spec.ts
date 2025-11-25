import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { expect } from '@jest/globals';


import { SessionService } from 'src/app/services/session.service';
import { SessionApiService } from '../features/sessions/services/session-api.service';
import { Session } from '../features/sessions/interfaces/session.interface';
import { FormComponent } from '../features/sessions/components/form/form.component';

@Component({ template: '' })
class DummyComponent {}

describe('FormComponent (integration)', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let router: Router;
  let snackBar: MatSnackBar;
  let sessionApiService: SessionApiService;

  const mockSessionService = {
    sessionInformation: {
      admin: true
    }
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FormComponent, DummyComponent],
      imports: [
        RouterTestingModule.withRoutes([
          { path: 'sessions', component: DummyComponent }
        ]),
        HttpClientTestingModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        MatSelectModule,
        MatSnackBarModule,
        NoopAnimationsModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        SessionApiService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;

    router = TestBed.inject(Router);
    snackBar = TestBed.inject(MatSnackBar);
    sessionApiService = TestBed.inject(SessionApiService);

    // Empêche le warning "Navigation triggered outside Angular zone"
    jest.spyOn(router, 'navigate').mockResolvedValue(true as never);
  });


  it('should create', () => {
    // Given
    // When
    fixture.detectChanges();

    // Then
    expect(component).toBeTruthy();
  });

  it('should redirect non admin user on init', () => {
    // Given
    mockSessionService.sessionInformation!.admin = false;
    const navigateSpy = jest.spyOn(router, 'navigate');

    // When
    component.ngOnInit();

    // Then
    expect(navigateSpy).toHaveBeenCalledWith(['/sessions']);
  });

  it('should init empty form in create mode', () => {
    // Given
    mockSessionService.sessionInformation!.admin = true;
    jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/create');

    // When
    component.ngOnInit();

    // Then
    expect(component.onUpdate).toBe(false);
    expect(component.sessionForm).toBeDefined();
    expect(component.sessionForm?.value).toEqual({
      name: '',
      date: '',
      teacher_id: '',
      description: ''
    });
  });

  it('should fetch session in update mode and init form', () => {
    // Given
    mockSessionService.sessionInformation!.admin = true;
    jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/update/1');

    const now = new Date();
    const session: Session = {
      id: 1,
      name: 'Updated Test Session',
      description: 'This is an updated test session',
      date: now,
      teacher_id: 2,
      users: [],
      createdAt: now,
      updatedAt: now
    };

    jest.spyOn(component['route'].snapshot.paramMap, 'get').mockReturnValue('1');
    const detailSpy = jest.spyOn(sessionApiService, 'detail').mockReturnValue(of(session));

    // When
    component.ngOnInit();

    // Then
    expect(detailSpy).toHaveBeenCalledWith('1');
    expect(component.onUpdate).toBe(true);

    const formValue = component.sessionForm!.value;
    expect(formValue.name).toBe(session.name);
    expect(formValue.description).toBe(session.description);
    expect(formValue.date).toBe(now.toISOString().split('T')[0]);
    expect(formValue.teacher_id).toBe(session.teacher_id);
  });

  it('should create session then exit page', () => {
    // Given
    mockSessionService.sessionInformation!.admin = true;
    jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/create');
    component.ngOnInit();

    const sessionFormValue = {
      name: 'Test Session',
      description: 'This is a test session',
      date: '2024-01-01',
      teacher_id: 1
    };

    component.sessionForm?.setValue(sessionFormValue);

    const createSpy = jest.spyOn(sessionApiService, 'create').mockReturnValue(
      of({
        ...sessionFormValue,
        date: new Date(sessionFormValue.date),
        id: 1,
        users: []
      } as Session)
    );

    const snackSpy = jest.spyOn(snackBar, 'open');
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true as never);

    // When
    component.submit();

    // Then
    expect(createSpy).toHaveBeenCalledWith(sessionFormValue);
    expect(snackSpy).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['sessions']);
  });

  it('should update session then exit page', () => {
    // Given
    mockSessionService.sessionInformation!.admin = true;
    jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/update/1');

    const now = new Date();
    const existingSession: Session = {
      id: 1,
      name: 'Old name',
      description: 'Old description',
      date: now,
      teacher_id: 1,
      users: []
    };

    jest.spyOn(component['route'].snapshot.paramMap, 'get').mockReturnValue('1');
    jest.spyOn(sessionApiService, 'detail').mockReturnValue(of(existingSession));

    component.ngOnInit();

    const updatedValue = {
      name: 'Updated name',
      description: 'Updated description',
      date: '2024-02-01',
      teacher_id: 2
    };

    component.sessionForm?.setValue(updatedValue);

    const updateSpy = jest.spyOn(sessionApiService, 'update').mockReturnValue(
      of({
        ...existingSession,
        ...updatedValue,
        date: new Date(updatedValue.date)
      } as Session)
    );

    const snackSpy = jest.spyOn(snackBar, 'open');
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true as never);

    // When
    component.submit();

    // Then
    expect(updateSpy).toHaveBeenCalledWith('1', updatedValue);
    expect(snackSpy).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['sessions']);
  });
});
