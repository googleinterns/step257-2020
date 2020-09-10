import { Pipe, PipeTransform } from '@angular/core';
import { Note } from './interfaces';
import { TranslateService } from './services/translate.service';

@Pipe({name: 'rangeLoop'})
export class RangeLoopPipe implements PipeTransform {
  transform(value): any {
    const res = [];
    for (let i = 0; i < value; ++i) {
      res.push(i);
    }
    return res;
  }
}

/**
 * When applied to Note, translates it to currently selected target language.
 * If no translation available, returns original note content
 */
@Pipe({name: 'noteContent', pure: false})
export class NoteContentPipe implements PipeTransform {
  constructor(private translateService: TranslateService) {}
  transform(note: Note): any {
    return this.translateService.getNoteTranslation(note.id);
  }
}
