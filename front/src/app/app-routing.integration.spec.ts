import { TestBed, ComponentFixture } from '@angular/core/testing';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

import { AppRoutingModule } from './app-routing.module';
import { MeComponent } from './components/me/me.component';
import { NotFoundComponent } from './components/not-found/not-found.component';
import { SessionService } from './services/session.service';
import { UserService } from './services/user.service';
import { AuthGuard } from './guards/auth.guard';
import { UnauthGuard } from './guards/unauth.guard';
import { StyleUtils } from '@angular/flex-layout';

@Component({ selector: 'test-host', template: '<router-outlet></router-outlet>' })
class TestHostComponent {}

describe('Routing when connected integration', () => {
	let fixture: ComponentFixture<TestHostComponent>;
	let router: Router;

	beforeEach(async () => {
		const sessionStub: Partial<SessionService> = { isLogged: true, sessionInformation: { id: 1 } as any };
		const userStub: Partial<UserService> = { getById: (_: string) => of({ id: 1, username: 'test' } as any) };

		await TestBed.configureTestingModule({
			imports: [
				RouterTestingModule,
				AppRoutingModule,
				HttpClientTestingModule,
				NoopAnimationsModule,
				MatCardModule,
				MatIconModule,
				MatSnackBarModule,
				MatButtonModule,
				MatFormFieldModule,
				MatInputModule,
				MatSelectModule
			],
			declarations: [TestHostComponent, MeComponent, NotFoundComponent],
			providers: [
				{ provide: SessionService, useValue: sessionStub },
				{ provide: UserService, useValue: userStub },
				{ provide: MatSnackBar, useValue: { open: jest.fn() } },
				AuthGuard,
				UnauthGuard,
				{ provide: StyleUtils, useValue: { lookupStyle: () => '', hasWrap: () => false, isFlowDirectionRTL: () => false, applyStyleToElement: () => {}, applyStyleToElements: () => {}, getFlowDirection: () => ['row', false] } }
			]
		}).compileComponents();

		router = TestBed.inject(Router);

		fixture = TestBed.createComponent(TestHostComponent);
		fixture.detectChanges();
	});

	it('navigates to /me when connected', async () => {
		await router.navigate(['/me']);
		fixture.detectChanges();
		expect(fixture.nativeElement.querySelector('app-me')).toBeTruthy();
	});

	it('navigates to /sessions when connected (lazy module)', async () => {
		await router.navigate(['/sessions']);
		fixture.detectChanges();
		expect(fixture.nativeElement.querySelector('app-list')).toBeTruthy();
	});

	it('renders 404 for unknown paths', async () => {
		await router.navigate(['/some/unknown/path']);
		fixture.detectChanges();
		expect(fixture.nativeElement.querySelector('app-not-found')).toBeTruthy();
	});
});

describe('Routing when NOT connected integration', () => {
	let fixture: ComponentFixture<TestHostComponent>;
	let router: Router;

	beforeEach(async () => {
		const sessionStub: Partial<SessionService> = { isLogged: false, sessionInformation: undefined };
		const userStub: Partial<UserService> = { getById: (_: string) => of({ id: 1, username: 'test' } as any) };

		await TestBed.configureTestingModule({
			imports: [
				RouterTestingModule,
				AppRoutingModule,
				HttpClientTestingModule,
				NoopAnimationsModule,
				MatCardModule,
				MatIconModule,
				MatSnackBarModule,
				MatButtonModule,
				MatFormFieldModule,
				MatInputModule,
				MatSelectModule
			],
			declarations: [TestHostComponent, MeComponent, NotFoundComponent],
			providers: [
				{ provide: SessionService, useValue: sessionStub },
				{ provide: UserService, useValue: userStub },
				{ provide: MatSnackBar, useValue: { open: jest.fn() } },
				AuthGuard,
				UnauthGuard,
				{ provide: StyleUtils, useValue: { lookupStyle: () => '', hasWrap: () => false, isFlowDirectionRTL: () => false, applyStyleToElement: () => {}, applyStyleToElements: () => {}, getFlowDirection: () => ['row', false] } }
			]
		}).compileComponents();

		router = TestBed.inject(Router);

		fixture = TestBed.createComponent(TestHostComponent);
		fixture.detectChanges();
	});

	it('redirects /me to /login when not connected', async () => {
		await router.navigate(['/me']);
		await fixture.whenStable();
		fixture.detectChanges();
		expect(fixture.nativeElement.querySelector('app-me')).toBeFalsy();
		expect(router.url).toMatch('/login');
	});

	it('redirects /sessions to /login when not connected', async () => {
		await router.navigate(['/sessions']);
		await fixture.whenStable();
		fixture.detectChanges();
		expect(fixture.nativeElement.querySelector('app-list')).toBeFalsy();
		expect(router.url).toMatch('/login');
	});
});

