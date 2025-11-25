import { TestBed } from '@angular/core/testing';
import { ComponentFixture } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';

import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

import { ListComponent } from '../features/sessions/components/list/list.component';
import { SessionService } from '../services/session.service';
import { SessionApiService } from '../features/sessions/services/session-api.service';

describe('ListComponent Integration', () => {
  let fixture: ComponentFixture<ListComponent>;
  let component: ListComponent;
  let httpMock: HttpTestingController;

  const mockSessionService = {
    sessionInformation: { admin: true, id: 1 }
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListComponent],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        BrowserAnimationsModule,
        MatCardModule,
        MatIconModule,
        MatButtonModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        SessionApiService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);

    fixture.detectChanges();
  });

  afterEach(() => httpMock.verify());

  it('should create', () => {
    // Given

    // When
    httpMock.expectOne('api/session').flush([]);

    // Then
    expect(component).toBeTruthy();
  });

  it('should show the Create button for admin users', () => {
    // Given

    // When
    httpMock.expectOne('api/session').flush([]);
    fixture.detectChanges();

    // Then
    const createBtn = fixture.nativeElement
      .querySelector('mat-card-header button[color="primary"] mat-icon');
    expect(createBtn.textContent.trim()).toBe('add');
  });

  it('should always show Detail button', () => {
    // Given
    const sessions = [
      { id: 1, name: 'Yoga', description: 'Relax', date: new Date(), teacher_id: 1, users: [] }
    ];

    // When
    httpMock.expectOne('api/session').flush(sessions);
    fixture.detectChanges();

    // Then
    const detailIcon = fixture.nativeElement
      .querySelector('.item mat-card-actions button mat-icon');
    expect(detailIcon.textContent.trim()).toBe('search');
  });

  it('should have correct routerLinks', () => {
    // Given
    const sessions = [
      { id: 1, name: 'Yoga', description: 'Relax', date: new Date(), teacher_id: 1, users: [] }
    ];

    // When
    httpMock.expectOne('api/session').flush(sessions);
    fixture.detectChanges();

    // Then
    const detailBtn = fixture.nativeElement
      .querySelector('.item mat-card-actions button[color="primary"]');

    expect(detailBtn.getAttribute('ng-reflect-router-link')).toContain('detail,1');
  });
});
