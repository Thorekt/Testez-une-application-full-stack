import { TestBed } from '@angular/core/testing';
import { ComponentFixture } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';

import { DetailComponent } from '../features/sessions/components/detail/detail.component';
import { SessionService } from '../services/session.service';
import { TeacherService } from '../services/teacher.service';
import { SessionApiService } from '../features/sessions/services/session-api.service';
import { ActivatedRoute } from '@angular/router';

describe('DetailComponent Integration', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let httpMock: HttpTestingController;

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
        HttpClientTestingModule,
        MatSnackBarModule,
        ReactiveFormsModule,
        BrowserAnimationsModule
      ],
      declarations: [DetailComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        SessionApiService,
        TeacherService,
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: () => '123'
              }
            }
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);

    fixture.detectChanges();
  });

  afterEach(() => httpMock.verify());

  function consumeInitRequests() {
    const req1 = httpMock.expectOne('api/session/123');
    req1.flush({ users: [], teacher_id: 99 });

    const req2 = httpMock.expectOne('api/teacher/99');
    req2.flush({ firstName: 'X', lastName: 'Y' });
  }

  it('fetches session and teacher on init', () => {
    const session = { users: [1, 2, 3], teacher_id: 42 } as any;
    const teacher = { firstName: 'John', lastName: 'Doe' } as any;

    const reqSession = httpMock.expectOne('api/session/123');
    reqSession.flush(session);

    const reqTeacher = httpMock.expectOne('api/teacher/42');
    reqTeacher.flush(teacher);

    expect(component.session).toEqual(session);
    expect(component.teacher).toEqual(teacher);
    expect(component.isParticipate).toBe(true);
  });

  it('participate triggers API call and refreshes session', () => {
    consumeInitRequests();

    jest.spyOn(component as any, 'fetchSession').mockImplementation(() => {});

    component.participate();

    const req = httpMock.expectOne('api/session/123/participate/1');
    req.flush({});

    expect((component as any).fetchSession).toHaveBeenCalled();
  });

  it('unParticipate triggers API call and refreshes session', () => {
    consumeInitRequests();

    jest.spyOn(component as any, 'fetchSession').mockImplementation(() => {});

    component.unParticipate();

    const req = httpMock.expectOne('api/session/123/participate/1');
    expect(req.request.method).toBe('DELETE');
    req.flush({});

    expect((component as any).fetchSession).toHaveBeenCalled();
  });

  it('delete triggers API delete and navigation', () => {
    consumeInitRequests();

    const snackSpy = jest.spyOn(component['matSnackBar'], 'open').mockImplementation(() => ({} as any));
    const routerSpy = jest.spyOn(component['router'], 'navigate').mockResolvedValue(true);

    component.delete();

    const req = httpMock.expectOne('api/session/123');
    req.flush({});

    expect(snackSpy).toHaveBeenCalled();
    expect(routerSpy).toHaveBeenCalledWith(['sessions']);
  });

  it('back calls window.history.back()', () => {
    consumeInitRequests();

    const spy = jest.spyOn(window.history, 'back').mockImplementation(() => {});

    component.back();

    expect(spy).toHaveBeenCalled();
  });
});
