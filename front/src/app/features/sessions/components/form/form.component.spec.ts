import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import {  ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';
import { SessionApiService } from '../../services/session-api.service';

import { FormComponent } from './form.component';
import { Session } from '../../interfaces/session.interface';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;

  const mockSessionService = {
    sessionInformation: {
      admin: true
    }
  } 

  beforeEach(async () => {
    await TestBed.configureTestingModule({

      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule, 
        MatSnackBarModule,
        MatSelectModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        SessionApiService
      ],
      declarations: [FormComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit form to create session and exit page', () => {
    // Given
    // submit part
    const now = new Date();
    const sessionValue = {
      id: 0,
      name: "Test Session",
      description: "This is a test session",
      date: new Date('2024-01-01'),
      teacher_id: 1,
      users: [],
      createdAt: now,
      updatedAt: now      
    } as Session;
    
    const sessionForm = {value: sessionValue}
    component.sessionForm = sessionForm as any;

    const createSpy = jest.spyOn(component['sessionApiService'], 'create').mockReturnValueOnce({
      subscribe: (callback: any) => callback(sessionValue)
    } as any);

    // exitPage part
    const matSnackBarSpy = jest.spyOn(component['matSnackBar'], 'open').mockImplementation(() => ({} as any));
    const routerNavigateSpy = jest.spyOn(component['router'], 'navigate').mockImplementation(() => Promise.resolve(true));

    // When
    component.submit();

    // Then
    expect(createSpy).toHaveBeenCalledWith(sessionValue);
    expect(matSnackBarSpy).toHaveBeenCalled();
    expect(routerNavigateSpy).toHaveBeenCalled();

  });

  it('should submit form to update session and exit page', () => {
    // Given
    // submit part
    component.onUpdate = true;
    const now = new Date();
    const sessionValue = {
      id: 1,
      name: "Updated Test Session",
      description: "This is an updated test session",
      date: new Date('2024-02-01'),
      teacher_id: 2,
      users: [],
      createdAt: now,
      updatedAt: now      
    } as Session;

    const sessionForm = {value: sessionValue}

    component.sessionForm = sessionForm as any;
    // cast to any to bypass the private modifier
    (component as any).id = '1';

    const updateSpy = jest.spyOn(component['sessionApiService'], 'update').mockReturnValueOnce({
      subscribe: (callback: any) => callback(sessionValue)
    } as any);

    // exitPage part
    const matSnackBarSpy = jest.spyOn(component['matSnackBar'], 'open').mockImplementation(() => ({} as any));
    const routerNavigateSpy = jest.spyOn(component['router'], 'navigate').mockImplementation(() => Promise.resolve(true));

    // When
    component.submit();

    // Then
    expect(updateSpy).toHaveBeenCalledWith('1', sessionValue);
    expect(matSnackBarSpy).toHaveBeenCalled();
    expect(routerNavigateSpy).toHaveBeenCalled();
  });

  it('should ngOntInit redirect non-admin user', () => {
    // Given
    mockSessionService.sessionInformation!.admin = false;
    const routerNavigateSpy = jest.spyOn(component['router'], 'navigate').mockImplementation(() => Promise.resolve(true));

    // When
    component.ngOnInit();

    // Then
    expect(routerNavigateSpy).toHaveBeenCalledWith(['/sessions']);
  });

  it('should ngOnInit in update mode fetch session and init form', () => {
    // Given
    mockSessionService.sessionInformation!.admin = true;
    const routerUrlSpy = jest.spyOn(component['router'], 'url', 'get').mockReturnValue('/sessions/update/1');
    const routeSnapshotParamMapGetSpy = jest.spyOn(component['route'].snapshot.paramMap, 'get').mockReturnValue('1');
    const now = new Date();
    const sessionValue = {
      id: 1,
      name: "Updated Test Session",
      description: "This is an updated test session",
      date: new Date('2024-02-01'),
      teacher_id: 2,
      users: [],
      createdAt: now,
      updatedAt: now      
    } as Session;
    const detailSpy = jest.spyOn(component['sessionApiService'], 'detail').mockReturnValueOnce({
      subscribe: (callback: any) => callback(sessionValue)
    } as any);

    // When
    component.ngOnInit();

    // Then
    expect(detailSpy).toHaveBeenCalledWith('1');
    expect(routeSnapshotParamMapGetSpy).toHaveBeenCalledWith('id');
    expect(routerUrlSpy).toHaveBeenCalled();

    expect(component.sessionForm?.value.name).toEqual(sessionValue.name);
    expect(component.sessionForm?.value.description).toEqual(sessionValue.description);
    expect(component.sessionForm?.value.date).toEqual(new Date(sessionValue.date).toISOString().split('T')[0]);
    expect(component.sessionForm?.value.teacher_id).toEqual(sessionValue.teacher_id);
    
    expect(component.onUpdate).toBe(true);
  });

  it('should ngOnInit in create mode init empty form', () => {
    // Given
    mockSessionService.sessionInformation!.admin = true;
    const routerUrlSpy = jest.spyOn(component['router'], 'url', 'get').mockReturnValue('/sessions/create');
    // When
    component.ngOnInit();

    // Then
    expect(routerUrlSpy).toHaveBeenCalled();
    expect(component.sessionForm?.value.name).toEqual('');
    expect(component.sessionForm?.value.description).toEqual('');
    expect(component.sessionForm?.value.date).toEqual('');
    expect(component.sessionForm?.value.teacher_id).toEqual('');
    expect(component.onUpdate).toBe(false);
  });
});