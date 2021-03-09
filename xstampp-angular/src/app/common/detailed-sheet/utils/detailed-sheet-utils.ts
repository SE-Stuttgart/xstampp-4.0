import { SheetMode } from '../detailed-sheet.component';

export class DetailedSheetUtils {

  constructor() {
  }

  static toggle(id: string, newId: string, mode: SheetMode, newMode: SheetMode): SheetMode {
    if (newId) {
      if (id !== newId) {
        return newMode;
      } else {
        if (mode === SheetMode.Closed || mode !== newMode) {
          return newMode;
        } else {
          return SheetMode.Closed;
        }
      }
    } else {
      if (mode === SheetMode.Closed) {
        return newMode;
      } else {
        return SheetMode.Closed;
      }
    }
  }

  static calculateLockExpiration(): string {
    const expiresInMin = 10;
    const date = new Date();
    let expTime = new Date(date.getTime() + (expiresInMin * 60 * 1000)).toISOString();
    expTime = expTime.substring(0, expTime.length - 1).replace('T', ' ');
    return expTime;
  }

  static generateSheetTitle(title) {
    title = title.toLowerCase()
      .split('-')
      .map((s) => s.charAt(0).toUpperCase() + s.substring(1))
      .join('-');
    return title;
  }
}
