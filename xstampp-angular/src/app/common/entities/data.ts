import { SheetMode } from '../detailed-sheet/detailed-sheet.component';

// TODO: Map typing
export interface DetailData {
    ent: any;
    id: number;
    mode: SheetMode;
    addedMap: Map<string, any>;
    deletedMap: Map<string, any>;
}

/**
 * @var title the title of the dropdown field
 * @var value the value of the dropdown
 */
export interface DDEvent {
    title: string;
    value: string;
}
