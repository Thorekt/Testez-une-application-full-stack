import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { SessionService } from 'src/app/services/session.service';
import { UserService } from 'src/app/services/user.service';

import { MeComponent } from './me.component';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;

  const mockSessionService = {
    logOut: () => {},
    sessionInformation: {
      admin: true,
      id: 1
    }
  }

  const mockUser = {
    id: 1,
    firstName: 'Test',
    lastName: 'User',
    email: 'test@example.com'
  } as any;

  const mockUserService = {
    getById: (_id: string) => of(mockUser),
    delete: (_id: string) => of({})
  }
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        MatSnackBarModule,
        HttpClientModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        RouterTestingModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: UserService, useValue: mockUserService }
      ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have user defined after ngOnInit', () => {
    // Given


    // When
    component.ngOnInit();

    // Then
    fixture.detectChanges();
    expect(component.user).toBeDefined();
  });

  it('should call back method', () => {
    // Given
    const backSpy = jest.spyOn(window.history as any, 'back').mockImplementation(() => {});


    // When
    component.back();

    // Then
    expect(backSpy).toHaveBeenCalled();

  });

  it('should delete user', () => {
    // Given
    const deleteSpy = jest.spyOn(mockUserService, 'delete').mockReturnValue(of({}));
    const snackBarSpy = jest.spyOn(component['matSnackBar'], 'open').mockImplementation(() => { return { onAction: () => of({}) } as any; });
    const logOutSpy = jest.spyOn(component['sessionService'], 'logOut').mockImplementation(() => {});
    const routerNavigateSpy = jest.spyOn(component['router'], 'navigate').mockImplementation(() => Promise.resolve(true));

    // When
    component.delete();

    // Then
    fixture.detectChanges();
    expect(deleteSpy).toHaveBeenCalledWith('1');
    expect(snackBarSpy).toHaveBeenCalled();
    expect(logOutSpy).toHaveBeenCalled();
    expect(routerNavigateSpy).toHaveBeenCalledWith(['/']);
  });
});
