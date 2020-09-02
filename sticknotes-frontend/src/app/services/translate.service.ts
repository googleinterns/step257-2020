import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TranslateService {

  constructor(private http: HttpClient) { }

  /**
   * Sends an array of texts and target language to the translate endpoint.
   * Returns translated text.
   */
  public translateArray(arrOfStrings: string[], targetLanguage): Observable<{result: string[]}> {
    const payload = {
      texts: arrOfStrings,
      targetLanguage: targetLanguage
    }
    return this.http.post<{result: string[]}>('api/translate/', payload);
  }
}
