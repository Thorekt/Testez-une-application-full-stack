import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { UserService } from './user.service';

describe('UserService', () => {
  let service: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[
        HttpClientModule
      ]
    });
    service = TestBed.inject(UserService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should getById call httpClient.get with correct URL', () => {
    // Given
    const httpClientGetSpy = jest.spyOn(service['httpClient'], 'get').mockReturnValueOnce({} as any);
    const id = '123';
    // When
    service.getById(id);
    // Then
    expect(httpClientGetSpy).toHaveBeenCalledWith(`api/user/${id}`);
  });

  it('should delete call httpClient.delete with correct URL', () => {
    // Given
    const httpClientDeleteSpy = jest.spyOn(service['httpClient'], 'delete').mockReturnValueOnce({} as any);
    const id = '123';
    // When
    service.delete(id);
    // Then
    expect(httpClientDeleteSpy).toHaveBeenCalledWith(`api/user/${id}`);
  });
});
