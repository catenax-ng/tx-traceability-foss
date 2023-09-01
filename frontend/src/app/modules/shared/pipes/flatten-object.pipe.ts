import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'flattenObject' })
export class FlattenObjectPipe implements PipeTransform {
  transform(inputObject: any): any {
    if (typeof inputObject !== 'object' || inputObject === null) {
      return inputObject;
    }

    const result: { [key: string]: any } = {};

    function recurse(current: any, property: string) {
      if (Object(current) !== current) {
        result[property] = current;
      } else if (Array.isArray(current)) {
        for (let i = 0; i < current.length; i++) {
          recurse(current[i], property + '[' + i + ']');
        }
        if (current.length === 0) {
          result[property] = [];
        }
      } else {
        let isEmpty = true;
        for (const p in current) {
          isEmpty = false;
          recurse(current[p], property ? property + '.' + p : p); // Remove dot for root-level properties
        }
        if (isEmpty && property) {
          result[property] = {};
        }
      }
    }

    recurse(inputObject, '');
    return result;
  }
}
