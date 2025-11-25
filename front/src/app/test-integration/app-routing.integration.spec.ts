import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { Location } from '@angular/common';

import { AppRoutingModule } from '../app-routing.module';
import { SessionService } from '../services/session.service';

describe('AppRoutingModule Routing Integration', () => {
  let router: Router;
  let location: Location;
  let sessionService: Partial<SessionService>;

  beforeEach(async () => {
    sessionService = { isLogged: false };

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([]),
        AppRoutingModule
      ],
      providers: [
        { provide: SessionService, useValue: sessionService }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    location = TestBed.inject(Location);
    await router.initialNavigation();
  });

  it('allows /me when logged in', async () => {
    sessionService.isLogged = true;
    await router.navigate(['/me']);
    expect(location.path()).toBe('/me');
  });

  it('redirects /me to /login when not logged in', async () => {
    sessionService.isLogged = false;
    await router.navigate(['/me']);
    expect(location.path()).toBe('/login');
  });

  it('allows /sessions when logged in', async () => {
    sessionService.isLogged = true;
    await router.navigate(['/sessions']);
    expect(location.path()).toBe('/sessions');
  });

  it('redirects /sessions to /login when not logged in', async () => {
    sessionService.isLogged = false;
    await router.navigate(['/sessions']);
    expect(location.path()).toBe('/login');
  });

  it('allows root when not logged in', async () => {
    sessionService.isLogged = false;
    await router.navigate(['/']);
    expect(location.path()).toBe('');
  });


  it('navigates to /404', async () => {
    await router.navigate(['/404']);
    expect(location.path()).toBe('/404');
  });

  it('redirects unknown routes to /404', async () => {
    await router.navigate(['/unknown']);
    expect(location.path()).toBe('/404');
  });
});
