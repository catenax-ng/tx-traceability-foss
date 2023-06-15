import { Pipe, PipeTransform } from '@angular/core';
import { Pagination } from '@core/model/pagination.model';

@Pipe({
  name: 'formatSemanticDataModelToProperCase'
})
export class FormatSemanticDataModelToProperCasePipe implements PipeTransform {
  transform(value: Pagination<any> | any): Pagination<any> | any {
    if (!value || !value.content || !Array.isArray(value.content)) {
      console.error('Invalid input. Expected a valid Pagination object.');
      return value;
    }

    const transformedContent = value.content.map(item => {
      if (!item.semanticDataModel || typeof item.semanticDataModel !== 'string') {
        console.error(`Invalid input for property 'semanticDataModel'. Expected a string.`);
        return item;
      }

      if (item.semanticDataModel.length === 0) {
        return item;
      }

      const firstLetter = item.semanticDataModel.charAt(0).toUpperCase();
      const restOfString = item.semanticDataModel.slice(1).toLowerCase();
      return {
        ...item,
        semanticDataModel: firstLetter + restOfString
      };
    });

    return {
      ...value,
      content: transformedContent
    };
  }
}
