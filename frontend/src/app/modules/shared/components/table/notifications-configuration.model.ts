import { TableFilterConfiguration } from '@shared/components/parts-table/parts-config.model';

export class NotificationsConfigurationModel extends TableFilterConfiguration {
  constructor() {
    const sortableColumns = {
      description: true,
      title: true,
      status: true,
      severity: true,
      createdDate: true,
      createdBy: true,
      createdByName: true,
      type: true,
      menu: false,
    };

    const dateFields = [ 'createdDate', 'targetDate' ];
    const singleSearchFields = [];
    super(sortableColumns, dateFields, singleSearchFields);
  }
}
