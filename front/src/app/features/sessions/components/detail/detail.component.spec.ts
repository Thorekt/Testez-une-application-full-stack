import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from '../../../../services/session.service';

import { DetailComponent } from './detail.component';

// Ajout minimal pour supprimer les warnings Angular Material
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let service: SessionService;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    }
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatSnackBarModule,
        ReactiveFormsModule,

        // Ajout pour corriger les warnings NG0304
        MatCardModule,
        MatIconModule,
        MatButtonModule
      ],
      declarations: [DetailComponent],
      providers: [{ provide: SessionService, useValue: mockSessionService }],
    }).compileComponents();

    service = TestBed.inject(SessionService);
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should back call window.history.back()', () => {
    const backSpy = jest.spyOn(window.history as any, 'back').mockImplementation(() => {});
    component.back();
    expect(backSpy).toHaveBeenCalled();
  });

  it('should delete session', () => {
    const deleteSpy = jest
      .spyOn(component['sessionApiService'], 'delete')
      .mockReturnValueOnce({
        subscribe: (callback: any) => callback({})
      } as any);

    const snackBarSpy = jest
      .spyOn(component['matSnackBar'], 'open')
      .mockImplementation(() => ({} as any));

    const routerNavigateSpy = jest
      .spyOn(component['router'], 'navigate')
      .mockImplementation(() => Promise.resolve(true));

    component.delete();

    fixture.detectChanges();
    expect(deleteSpy).toHaveBeenCalledWith(component.sessionId);
    expect(snackBarSpy).toHaveBeenCalled();
    expect(routerNavigateSpy).toHaveBeenCalledWith(['sessions']);
  });

  it('should participate to session', () => {
    const participateSpy = jest
      .spyOn(component['sessionApiService'], 'participate')
      .mockReturnValueOnce({
        subscribe: (callback: any) => callback({})
      } as any);

    const fetchSessionSpy = jest
      .spyOn(component as any, 'fetchSession')
      .mockImplementation(() => {});

    component.participate();

    fixture.detectChanges();
    expect(participateSpy).toHaveBeenCalledWith(component.sessionId, component.userId);
    expect(fetchSessionSpy).toHaveBeenCalled();
  });

  it('should unParticipate to session', () => {
    const unParticipateSpy = jest
      .spyOn(component['sessionApiService'], 'unParticipate')
      .mockReturnValueOnce({
        subscribe: (callback: any) => callback({})
      } as any);

    const fetchSessionSpy = jest
      .spyOn(component as any, 'fetchSession')
      .mockImplementation(() => {});

    component.unParticipate();

    fixture.detectChanges();
    expect(unParticipateSpy).toHaveBeenCalledWith(component.sessionId, component.userId);
    expect(fetchSessionSpy).toHaveBeenCalled();
  });

  it('should fetch session and teacher details', () => {
    const session = {
      users: [1, 2, 3],
      teacher_id: 10
    } as any;

    const teacher = {
      name: 'John Doe'
    } as any;

    const sessionDetailSpy = jest
      .spyOn(component['sessionApiService'], 'detail')
      .mockReturnValueOnce({
        subscribe: (callback: any) => callback(session)
      } as any);

    const teacherDetailSpy = jest
      .spyOn(component['teacherService'], 'detail')
      .mockReturnValueOnce({
        subscribe: (callback: any) => callback(teacher)
      } as any);

    component.ngOnInit();

    fixture.detectChanges();
    expect(sessionDetailSpy).toHaveBeenCalledWith(component.sessionId);
    expect(component.session).toEqual(session);
    expect(teacherDetailSpy).toHaveBeenCalledWith('10');
    expect(component.teacher).toEqual(teacher);
  });
});
