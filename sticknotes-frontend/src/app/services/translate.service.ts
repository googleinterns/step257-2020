// Copyright 2020 Google LLC

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, combineLatest } from 'rxjs';
import { SharedBoardService } from './shared-board.service';

@Injectable({
  providedIn: 'root'
})
export class TranslateService {
  // hashtable which has translation for every note
  // note.id mapped to note translation
  private notesTranslation = {};
  // another hashtable that stores the original version of notes content
  private notesOriginalContent = {};
  // current language of notes in notesTranslation map
  private currentTranslation = 'original';
  public notesTargetLanguage = new BehaviorSubject<string>(null);

  /**
   * The list of languages which notes can be translated to.
   * To add more language, set value and viewValue of the language
   */
  public translateLanguages = [
    { value: 'original', viewValue: 'Original language' },
    { value: 'en', viewValue: 'English' },
    { value: 'hr', viewValue: 'Hrvatski' },
    { value: 'pl', viewValue: 'Polski' },
    { value: 'ro', viewValue: 'Română' },
    { value: 'te', viewValue: 'తెలుగు' },
    { value: 'uk', viewValue: 'Українська' },
    { value: 'zh', viewValue: '中文' },
  ];

  constructor(private http: HttpClient, private sharedBoard: SharedBoardService) {
    // create subscription on board updates and translation updates
    combineLatest([this.sharedBoard.boardObservable(), this.notesTargetLanguage.asObservable()]).subscribe(([board, lng]) => {
      // when notes are updated, translation may be required to run again if some notes' contents were updated
      if (!board) {
        return;
      }
      const notesToTranslate = [];
      board.notes.forEach(note => {
        // note content was updated, need to re-run translation for this note
        if (note.content !== this.notesOriginalContent[note.id]) {
          // if translation enabled, run it again for this note because content was changed
          if (this.translationEnabled) {
            notesToTranslate.push(note);
          }
          // update original note content map
          this.notesOriginalContent[note.id] = note.content;
        }
        // if new translation was requested, translate note
        else if (this.translationEnabled && this.currentTranslation !== this.notesTargetLanguage.value) {
          notesToTranslate.push(note);
        }
      });
      if (notesToTranslate.length) {
        // need to run translation
        const textsToTranslate = [];
        notesToTranslate.forEach(note => {
          textsToTranslate.push(note.content);
        });
        // send array of note content to the translate api and update local translation hashtable
        this.translateArray(textsToTranslate, lng).subscribe(data => {
          for (let i = 0; i < notesToTranslate.length; ++i) {
            const note = notesToTranslate[i];
            this.notesTranslation[note.id] = data.result[i];
          }
        });
      }
      this.currentTranslation = lng;
    });
  }

  /**
   * Returns translation for note with given id.
   * If no translation enabled, returns note's original content
   */
  public getNoteTranslation(noteId: string): string {
    if (this.translationEnabled) {
      return this.notesTranslation[noteId];
    }
    return this.notesOriginalContent[noteId];
  }

  /**
   * Sends an array of texts and target language to the translate endpoint.
   * Returns translated text.
   */
  public translateArray(arrOfStrings: string[], targetLanguage): Observable<{ result: string[] }> {
    const payload = {
      texts: arrOfStrings,
      targetLanguage: {targetLanguage}
    };
    return this.http.post<{ result: string[] }>('api/translate/', payload);
  }

  /**
   * Returns true if translation is enabled, false otherwise
   */
  get translationEnabled(): boolean {
    return (this.notesTargetLanguage.value !== null && this.notesTargetLanguage.value !== 'original');
  }
}
