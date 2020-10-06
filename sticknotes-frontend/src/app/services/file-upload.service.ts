// Copyright 2020 Google LLC

import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class FileUploadService {

  constructor(private http: HttpClient) { }

  /**
   * Uploads file to the cloud storage and returns the link to the file
   */
  public uploadFile(file: File): Observable<{fileUrl: string}> {
    const formData: FormData = new FormData();
    formData.append('file', file, file.name);
    return this.http.post<{fileUrl: string}>('api/file-upload/', formData);
  }
}
