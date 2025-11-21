import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';

import { AppComponent } from './app.component';


describe('AppComponent test unitaire', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatToolbarModule
      ],
      declarations: [
        AppComponent
      ],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should $isLogged return observable of boolean', (done) => {
    // Given
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;

    // When
    const isLogged$ = app.$isLogged();

    // Then
    isLogged$.subscribe(value => {
      expect(typeof value).toBe('boolean');
      done();
    });

  });

  it('should logout call sessionService.logOut and router.navigate', () => {
    // Given
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    const logOutSpy = jest.spyOn(app['sessionService'], 'logOut').mockImplementation(() => {});
    const routerNavigateSpy = jest.spyOn(app['router'], 'navigate').mockImplementation(() => Promise.resolve(true));

    // When
    app.logout();

    // Then
    expect(logOutSpy).toHaveBeenCalled();
    expect(routerNavigateSpy).toHaveBeenCalledWith(['']);
  });

});
