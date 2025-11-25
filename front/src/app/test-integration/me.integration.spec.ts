import { TestBed } from '@angular/core/testing';
import { ComponentFixture } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';

import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBarModule } from '@angular/material/snack-bar';

import { MeComponent } from '../components/me/me.component';
import { SessionService } from '../services/session.service';
import { UserService } from '../services/user.service';

describe('MeComponent Integration', () => {
  let fixture: ComponentFixture<MeComponent>;
  let component: MeComponent;
  let httpMock: HttpTestingController;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1,
      email: 'session@example.com'
    },
    logOut: jest.fn()
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([]),
        BrowserAnimationsModule,
        MatCardModule,
        MatIconModule,
        MatSnackBarModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        UserService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);

    fixture.detectChanges();
  });

  afterEach(() => httpMock.verify());

  it('fetches user on init', () => {
    // Given
    const mockUser = {
      id: 1,
      firstName: 'Test',
      lastName: 'User',
      email: 'test@example.com',
      createdAt: new Date(),
      updatedAt: new Date(),
      admin: true
    };

    // When
    const req = httpMock.expectOne('api/user/1');
    req.flush(mockUser);
    fixture.detectChanges();

    // Then
    expect(component.user).toEqual(mockUser);
  });

  it('calls back() and triggers window.history.back()', () => {
    // Given
    const backSpy = jest.spyOn(window.history, 'back').mockImplementation(() => {});

    // When
    const req = httpMock.expectOne('api/user/1');
    req.flush({
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@example.com'
    });
    fixture.detectChanges();

    component.back();

    // Then
    expect(backSpy).toHaveBeenCalled();
  });

  it('deletes user and navigates to home', async () => {
    // Given
    const navigateSpy = jest
      .spyOn(component['router'], 'navigate')
      .mockResolvedValue(true);

    const snackSpy = jest
      .spyOn(component['matSnackBar'], 'open')
      .mockReturnValue({ onAction: () => {} } as any);

    // When
    // 1. Flush initial GET
    const getReq = httpMock.expectOne('api/user/1');
    getReq.flush({
      id: 1,
      firstName: 'Test',
      lastName: 'User',
      email: 'test@example.com'
    });
    fixture.detectChanges();

    // 2. Trigger delete
    component.delete();

    // DELETE request
    const deleteReq = httpMock.expectOne('api/user/1');
    expect(deleteReq.request.method).toBe('DELETE');
    deleteReq.flush({});

    fixture.detectChanges();

    // Then
    expect(snackSpy).toHaveBeenCalled();
    expect(mockSessionService.logOut).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['/']);
  });

  it('should display user info in template', () => {
    // Given
    const mockUser = {
      id: 1,
      firstName: 'John',
      lastName: 'DOE',
      email: 'john@example.com'
    };

    // When
    const req = httpMock.expectOne('api/user/1');
    req.flush(mockUser);
    fixture.detectChanges();

    const text = fixture.nativeElement.textContent;

    // Then
    expect(text).toContain('John DOE');
    expect(text).toContain('john@example.com');
  });
});
