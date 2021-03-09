export enum ColumnType {
  Text,
  Project_Selection,
  Checkbox,
  Button,
  Date_Time,
  StateIcon,
  Group_Deletion,
  Icon,
}

export class TableColumn {
  title: string;
  key: string;
  type: ColumnType;
  style?: any;
  userName?: string;
}
