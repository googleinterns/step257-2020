// Copyright 2020 Google LLC

import { Vector2 } from './vector';
import { FormControl } from '@angular/forms';

export function getTranslateValues(element: HTMLElement): Vector2 {
  const style = window.getComputedStyle(element);
  const matrix = style.transform || style.webkitTransform;

  // No transform property. Simply return 0 values.
  if (matrix === 'none') {
    return new Vector2(0, 0);
  }

  // Can either be 2d or 3d transform
  const matrixType = matrix.includes('3d') ? '3d' : '2d';
  const matrixValues = matrix.match(/matrix.*\((.+)\)/)[1].split(', ');

  if (matrixType === '2d') {
    return new Vector2(Number(matrixValues[4]), Number(matrixValues[5]));
  }

  // 3d matrices have 16 values
  // The 13th, 14th, and 15th values are X, Y, and Z
  if (matrixType === '3d') {
    return new Vector2(Number(matrixValues[12]), Number(matrixValues[13]));
  }
}

export function noSpacesValidator(control: FormControl): any {
  const isWhitespace = (control.value || '').trim().length === 0;
  const isValid = !isWhitespace;
  return isValid ? null : { whitespace: true };
}
