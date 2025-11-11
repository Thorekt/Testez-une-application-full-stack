import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule, } from '@angular/router/testing';
import { expect } from '@jest/globals'; 
import { SessionService } from '../../../../services/session.service';

import { DetailComponent } from './detail.component';


describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>; 
  let service: SessionService;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatSnackBarModule,
        ReactiveFormsModule
      ],
      declarations: [DetailComponent], 
      providers: [{ provide: SessionService, useValue: mockSessionService }],
    })
      .compileComponents();
      service = TestBed.inject(SessionService);
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should back call window.history.back()', () => {
    // Given
    const backSpy = jest.spyOn(window.history as any, 'back').mockImplementation(() => {});


    // When
    component.back();

    // Then
    expect(backSpy).toHaveBeenCalled();
  });

  it('should delete session', () => {
    // Given
    const deleteSpy = jest.spyOn(component['sessionApiService'], 'delete').mockReturnValueOnce({
      subscribe: (callback: any) => callback({})
    } as any);
    const snackBarSpy = jest.spyOn(component['matSnackBar'], 'open').mockImplementation(() => ({} as any));
    const routerNavigateSpy = jest.spyOn(component['router'], 'navigate').mockImplementation(() => Promise.resolve(true));

    // When
    component.delete();

    // Then
    fixture.detectChanges();
    expect(deleteSpy).toHaveBeenCalledWith(component.sessionId);
    expect(snackBarSpy).toHaveBeenCalled();
    expect(routerNavigateSpy).toHaveBeenCalledWith(['sessions']);
  });
});

