import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { TeacherService } from './teacher.service';

describe('TeacherService', () => {
  let service: TeacherService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[
        HttpClientModule
      ]
    });
    service = TestBed.inject(TeacherService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should all call httpClient.get with correct URL', () => {
    // Given
    const httpClientGetSpy = jest.spyOn(service['httpClient'], 'get').mockReturnValueOnce({} as any);

    // When
    service.all();

    // Then
    expect(httpClientGetSpy).toHaveBeenCalledWith('api/teacher');
  });

  it('should detail call httpClient.get with correct URL', () => {
    // Given
    const httpClientGetSpy = jest.spyOn(service['httpClient'], 'get').mockReturnValueOnce({} as any);
    const id = '123';

    // When
    service.detail(id);

    // Then
    expect(httpClientGetSpy).toHaveBeenCalledWith(`api/teacher/${id}`);
  });
});
