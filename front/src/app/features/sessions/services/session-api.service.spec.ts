import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionApiService } from './session-api.service';

describe('SessionsService', () => {
  let service: SessionApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[
        HttpClientModule
      ]
    });
    service = TestBed.inject(SessionApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should all call HttpClient get method', () => {
    // Given
    const httpClientSpy = jest.spyOn(service['httpClient'], 'get').mockReturnValueOnce({} as any);

    // When
    const observable = service.all();

    // Then
    expect(httpClientSpy).toHaveBeenCalled();
    expect(observable).toBeDefined();

  });

  it('should detail call HttpClient get method with id in url', () => {
    // Given
    const httpClientSpy = jest.spyOn(service['httpClient'], 'get').mockReturnValueOnce({} as any);
    const id = '123';

    // When
    const observable = service.detail(id);

    // Then
    expect(httpClientSpy).toHaveBeenCalledWith(`api/session/${id}`);
    expect(observable).toBeDefined();
  });

  it('should delete call HttpClient delete method with id in url', () => {
    // Given
    const httpClientSpy = jest.spyOn(service['httpClient'], 'delete').mockReturnValueOnce({} as any);
    const id = '123';

    // When
    const observable = service.delete(id);

    // Then
    expect(httpClientSpy).toHaveBeenCalledWith(`api/session/${id}`);
    expect(observable).toBeDefined();
  });

  it('should create call HttpClient post method', () => {
    // Given
    const httpClientSpy = jest.spyOn(service['httpClient'], 'post').mockReturnValueOnce({} as any);
    const session = { } as any;

    // When
    const observable = service.create(session);

    // Then
    expect(httpClientSpy).toHaveBeenCalledWith('api/session', session);
    expect(observable).toBeDefined(); 
  });

  it('should update call HttpClient put method with id in url', () => {
    // Given
    const httpClientSpy = jest.spyOn(service['httpClient'], 'put').mockReturnValueOnce({} as any);
    const id = '123';

    // When
    const observable = service.update(id, {} as any);

    // Then
    expect(httpClientSpy).toHaveBeenCalledWith(`api/session/${id}`, {});
    expect(observable).toBeDefined();
  });

  it('should participate call HttpClient post method with id and userId in url', () => {
    // Given
    const httpClientSpy = jest.spyOn(service['httpClient'], 'post').mockReturnValueOnce({} as any);
    const id = '123';
    const userId = '456';

    // When
    const observable = service.participate(id, userId);

    // Then
    expect(httpClientSpy).toHaveBeenCalledWith(`api/session/${id}/participate/${userId}`, null);
    expect(observable).toBeDefined();
  });

  it('should unParticipate call HttpClient delete method with id and userId in url', () => {
    // Given
    const httpClientSpy = jest.spyOn(service['httpClient'], 'delete').mockReturnValueOnce({} as any);
    const id = '123';
    const userId = '456';

    // When
    const observable = service.unParticipate(id, userId);

    // Then
    expect(httpClientSpy).toHaveBeenCalledWith(`api/session/${id}/participate/${userId}`);
    expect(observable).toBeDefined();
  });
});
