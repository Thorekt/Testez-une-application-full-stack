import { TestBed } from "@angular/core/testing";
import { AuthService } from "./auth.service";
import { expect } from '@jest/globals';
import { HttpClient } from "@angular/common/http";

describe('AuthService', () => {
    let service: AuthService;
    let mockedHttpClient: HttpClient;

    beforeEach(() => {
        mockedHttpClient = {
            post: (_url: string, _body: any) => { return {} as any; },
        } as HttpClient;
        service =  new AuthService(mockedHttpClient);        
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should register call httpClient.post with correct url and body', () => {
        // Given
        const registerRequest = {
            email: 'test@example.com',
            password: 'password123',
            firstName: 'Test',
            lastName: 'User'
        };

        const httpPostSpy = jest.spyOn(mockedHttpClient, 'post').mockReturnValue({} as any);

        // When
        const observable = service.register(registerRequest);

        // Then
        expect(httpPostSpy).toHaveBeenCalledWith('api/auth/register', registerRequest);
        expect(observable).toBeDefined();
    });

    it('should login call httpClient.post with correct url and body', () => {
        // Given
        const loginRequest = {
            email: 'test@example.com',
            password: 'password123'
        };

        const httpPostSpy = jest.spyOn(mockedHttpClient, 'post').mockReturnValue({} as any);

        // When
        const observable = service.login(loginRequest);

        // Then
        expect(httpPostSpy).toHaveBeenCalledWith('api/auth/login', loginRequest);
        expect(observable).toBeDefined();
    });
});