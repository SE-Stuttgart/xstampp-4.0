import { Injectable } from '@angular/core';
import { FilterPredicate } from './filter-types';
import { MainTableEntity } from 'src/app/types/local-types';
import { BehaviorSubject, Observable } from 'rxjs';

/**
* A recursive-descent parser for expressions of the grammar below.
* There is exactly one parsing function per production (line) of the grammar.
* It parses queries made by the user against the main-table's data elements
* and returns a function which the main-table's MatTableDataSource then uses to
* decide whether to display a row corresponding to a data element.
*
* Grammar:
* orExpr = orExpr "or" andExpr | andExpr
* andExpr = andExpr "and" notExpr | notExpr
* notExpr = "!" bracketExpr | bracketExpr
* bracketExpr = "(" orExpr ")"" | value
* value = stringLiteral | number | compositeNumber | interval | entityState  | lastdays
* stringLiteral e.g. "adfjasdfkl" or '123234k asdx asdffö'
* number is a number like 93024, notice that precdeing +- is not supported
* compositeNumber e.g. 3.4
* interval = "["number, number"]""
* entityState = TODO | DOING | DONE
* lastdays = "last"(\d+)
*/
@Injectable({
  providedIn: 'root'
})
export class FilterParserService {

  constructor() { }
  /**
  * Below are regular expressions used by the parse to determine which if any
  * rule of the grammar is to be evaluated. The bracket groups () are used to
  * capture substrings of interest which are then recursively parsed.
  */
  static notRE: RegExp = /^\s*!(.+)$/;
  static bracketRE: RegExp = /^\s*\((.+)\)\s*$/;
  // corresponding to the value rule in the grammar
  static stringLiteralRE: RegExp = /^\s*"(.*)"\s*$/;
  static numberRE: RegExp = /^\s*(\d+)\s*$/;
  static compositeNumberRE: RegExp = /^\s*(\d+\.\d+)\s*$/;
  static entityStateRE: RegExp = /^\s*(todo|doing|done)\s*$/;
  static intervalRE: RegExp = /^\s*\[(\d+),(\d+)\]\s*$/;
  static literalRE: RegExp = /^s*r"(\S+)"\s*$/;
  static lastDaysRE: RegExp = /^\s*last(\d+)\s*$/;

  static parseValueFunctions: ((query: String) => FilterPredicate)[] = [FilterParserService.parseStringLiteral, FilterParserService.parseNumber,
    FilterParserService.parseEntityState, FilterParserService.parseInterval,
    FilterParserService.parseLastDays, FilterParserService.parseCompositeNumber];

  // These Observables are used to signal the result of the last parsing process to interested components
  public parsingSuccessSubject: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);
  public parsingSuccess$: Observable<boolean> = this.parsingSuccessSubject.asObservable();
  public parsingErrorMsgSubject: BehaviorSubject<string> = new BehaviorSubject<string>('');
  public parsingErrorMsg$: Observable<string> = this.parsingErrorMsgSubject.asObservable();

  /**
  * Returns number i s.t. term is leftmost substring of query starting at pos i,
  * iff there are an even number of quotes and as many open as closed brackets left to pos i.
  * Else returns -1
  * orExpr = orExpr "or" andExpr | andExpr
  * @param query
  */
  static findNotNested(query: string, term: string): number {
    let quotes: number = 0;
    let bracketNesting: number = 0;
    let brace_summand: number;
    for (let i = 0; i < query.length; i++) {
      let char: string = query.charAt(i);
      switch (char) {
        case '"': quotes = quotes + 1; break;
        case '(': if (quotes % 2 == 0) { bracketNesting = bracketNesting + 1; }
                  break;
        case ')': if (quotes % 2 == 0) { bracketNesting = bracketNesting + -1; }
                  break;
      }
      if ((quotes % 2) === 0 && bracketNesting === 0 && query.slice(i, i + term.length) === term ) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Production: orExpr = orExpr "or" andExpr | andExpr
   * @param query
   */
  static parseOr(query: string): FilterPredicate {
    let orPos = FilterParserService.findNotNested(query, 'or');
    // optimization > 0 is also correct
    if (orPos > 2) {
      let lhs = query.slice(0, orPos);
      let rhs = query.slice(orPos + 2);
      let lpred = this.parseAnd(lhs);
      if (lpred) {
        let rpred = this.parseOr(rhs);
        if (rpred) {
          let pred = this.orPredicate(lpred, rpred);
          if (pred) { return pred; }
        }
      }
    }
    return this.parseAnd(query);
  }

  /**
  * Production: andExpr = andExpr "and" notExpr | notExpr
  * @param query
  */
  static parseAnd(query: string): FilterPredicate {
    let andPos = FilterParserService.findNotNested(query, 'and');
    // optimization > 0 is also correct
    if (andPos > 3) {
      let lhs = query.slice(0, andPos);
      let rhs = query.slice(andPos + 3);
      let lpred = this.parseNot(lhs);
      if (lpred) {
        let rpred = this.parseAnd(rhs);
        if (rpred) {
          let pred = this.andPredicate(lpred, rpred);
          if (pred) { return pred; }
        }
      }
    }
    return this.parseNot(query);
  }

  /**
  * Production notExpr = "!" bracketExpr | bracketExpr
  * @param query
  */
  static parseNot(query: string): FilterPredicate {
    let m: RegExpMatchArray = this.notRE.exec(query);
    if (m) {
      let negatedPred = this.parseBrackets(m[1]);
      if (negatedPred) {
        return this.notPredicate(negatedPred);
      }
    }
    return this.parseBrackets(query);
  }

  /**
  * bracketExpr = "(" orExpr ")" | value
  * @param query
  */
  static parseBrackets(query: string): FilterPredicate {
    let m: RegExpMatchArray = this.bracketRE.exec(query);
    if (m) {
      let pred = this.parseOr(m[1]);
      if (pred) { return pred; }
    }
    return this.parseValue(query);
  }

  /**
  * Covers the base cases of recursion. The leafs of the parsing tree. The functions invoced are not recursive.
  * value = stringLiteral | number | entityState  | interval | regex | lastdays
  * @param query
  */
  static parseValue(query: string): FilterPredicate {
    // let m: RegExpMatchArray = this.stringLiteralRE.exec(query);
    // stringLiteral
    let predicate: FilterPredicate;
    for (let f of this.parseValueFunctions) {
      predicate = f(query);
      if (predicate) {
        return predicate;
      }
    }
  }

  static parseStringLiteral(query: string): FilterPredicate {
    let m: RegExpMatchArray = FilterParserService.stringLiteralRE.exec(query);
    if (m) {
      // return FilterParserService.stringLiteralPredicate(m[1]);
      return (obj: MainTableEntity, _: string): boolean => {
        let monthStr = String(new Date(obj.lastEdited)).split(' ')[1];
        console.log('date:' + monthStr, 'against: ' + m[1]);
        return (m[1].length > 2 && obj.description && obj.description.toLowerCase().indexOf(m[1]) > -1) ||
        FilterParserService.sEquals([obj.name, obj.lastEditor, obj.controlActionName, obj.conversion, obj.rule,
          obj.constraintName, obj.referenceNumber, monthStr, obj.state], m[1]); };
      }
    }

    static parseNumber(query: string): FilterPredicate {
      let m: RegExpMatchArray = FilterParserService.numberRE.exec(query);
      if (m) {
        return (obj: MainTableEntity, _: string): boolean => {
          return obj.id == m[1].toString() || obj.subHazDisplayId == m[1] || obj.subSafetyContraintId == m[1] ||
          obj.parentSubSafetyConstraintId == m[1] || obj.controlActionId == m[1] || obj.parentId == m[1] || obj.referenceNumber == m[1]; };
        }
      }

      static parseCompositeNumber(query: string): FilterPredicate {
        let m: RegExpMatchArray = FilterParserService.compositeNumberRE.exec(query);
        if (m) {
          return (obj: MainTableEntity, _: string): boolean => obj.subSafetyConstraintDisplayId === m[1] || obj.subHazDisplayId === m[1];
        }
      }

      static parseEntityState(query: string): FilterPredicate {
        let m: RegExpMatchArray = FilterParserService.entityStateRE.exec(query);
        if (m) {
          return (obj: MainTableEntity, _: string): boolean => obj.state.toLowerCase() === m[1].toLowerCase();
        }
      }

      static parseInterval(query: string): FilterPredicate {
        let m: RegExpMatchArray = FilterParserService.intervalRE.exec(query.replace(/\s/g, ''));
        if (m) {
          let rangeStart = parseInt(m[1]);
          let rangeEnd = parseInt(m[2]);
          if (rangeEnd - rangeStart + 1 > 1000) {
            throw new Error('The specified interval is too large');
          }
          return (obj: MainTableEntity, _: string): boolean => {
            let n: number = obj.referenceNumber ? parseInt(obj.referenceNumber) : parseInt(obj.id);
            return FilterParserService.range(rangeEnd - rangeStart + 1, rangeStart).indexOf(n) > -1;
          };
        }
      }

      static parseLastDays(query: string): FilterPredicate {
        let m = FilterParserService.lastDaysRE.exec(query);
        if (m) {
          let dayCount = parseInt(m[1]);
          if (dayCount > 1200) {
            throw new Error('You can go back at most 1200 days using "last"');
          }
          return (obj: MainTableEntity, _: string): boolean => FilterParserService.daysSince(new Date(obj.lastEdited)) < parseInt(m[1]);
        }
      }

      /**
      * The code below is no longer concerned with parsing but with giving the query language a meaning.
      * The defined static methods return FilterPredicate that return true when passed an element that
      * matches their meaning.
      */

      /**
      * Returns array [startAt, startAt+1, ..., startAt+size]
      * @param size
      * @param startAt
      */
      static range(size, startAt = 0): Array<number> {
        return [...Array.from(Array(size).keys())].map(i => i + startAt);
      }

      /**
      * A predicate returning true if lpred OR rpred return true
      * @param lpred
      * @param rpred
      */
      static orPredicate(lpred: FilterPredicate, rpred: FilterPredicate): FilterPredicate {
        return (obj: MainTableEntity, filter: string): boolean => (lpred(obj, filter) || rpred(obj, filter));
      }

      /**
      * A predicate returning true if lpred AND rpred return true
      * @param lpred
      * @param rpred
      */
      static andPredicate(lpred: FilterPredicate, rpred: FilterPredicate): FilterPredicate {
        return (obj: MainTableEntity, filter: string): boolean => (lpred(obj, filter) && rpred(obj, filter));
      }

      /**
      * A predicate returning true iff pred returns false
      * @param pred
      */
      static notPredicate(pred: FilterPredicate): FilterPredicate {
        return (obj: MainTableEntity, filter: string): boolean => !pred(obj, filter);
      }

      static stringLiteralPredicate(query: string): FilterPredicate {
        /**
        * Checks if a data object matches the data source's filter string. By default, each data object
        * is converted to a string of its properties and returns true if the filter has
        * at least one occurrence in that string. By default, the filter string has its whitespace
        * trimmed and the match is case-insensitive. May be overridden for a custom implementation of
        * filter matching.
        * @param data Data object used to check against the filter.
        * @param filter Filter string that has been set on the data source.
        * @returns Whether the filter matches against the data
        */
        return (data: any, _: string): boolean => {
          // Transform the data into a lowercase string of all property values.
          const dataStr = Object.keys(data).reduce((currentTerm: string, key: string) => {
            // Use an obscure Unicode character to delimit the words in the concatenated string.
            // This avoids matches where the values of two columns combined will match the user's query
            // (e.g. `Flute` and `Stop` will match `Test`). The character is intended to be something
            // that has a very low chance of being typed in by somebody in a text field. This one in
            // particular is "White up-pointing triangle with dot" from
            // https://en.wikipedia.org/wiki/List_of_Unicode_characters
            return currentTerm + (data as { [key: string]: any})[key] + '◬';
          }, '').toLowerCase();

          // Transform the filter by converting it to lowercase and removing whitespace.
          console.log('query: ' + query + ' cT: ' + dataStr);

          return dataStr.indexOf(query) != -1;
        };

      }
      /**
      * Checks if a data object matches the data source's filter string. By default, each data object
      * is converted to a string of its properties and returns true if the filter has
      * at least one occurrence in that string. By default, the filter string has its whitespace
      * trimmed and the match is case-insensitive. May be overridden for a custom implementation of
      * filter matching.
      * @param data Data object used to check against the filter.
      * @param filter Filter string that has been set on the data source.
      * @returns Whether the filter matches against the data
      */
      static filterPredicate: ((data: any, filter: string) => boolean) = (data: any, filter: string): boolean => {
        // Transform the data into a lowercase string of all property values.
        const dataStr = Object.keys(data).reduce((currentTerm: string, key: string) => {
          // Use an obscure Unicode character to delimit the words in the concatenated string.
          // This avoids matches where the values of two columns combined will match the user's query
          // (e.g. `Flute` and `Stop` will match `Test`). The character is intended to be something
          // that has a very low chance of being typed in by somebody in a text field. This one in
          // particular is "White up-pointing triangle with dot" from
          // https://en.wikipedia.org/wiki/List_of_Unicode_characters
          return currentTerm + (data as { [key: string]: any})[key] + '◬';
        }, '').toLowerCase();
        // Transform the filter by converting it to lowercase and removing whitespace.
        const transformedFilter = filter.trim().toLowerCase();
        return dataStr.indexOf(transformedFilter) != -1;
      }

      /**
      * Returns number of days since date
      * @param date
      */
      static daysSince(date: Date): number {
        return (new Date().getTime() - date.getTime()) / (1000 * 3600 * 24);
      }

      /**
      * Returns true iff there is any c in candidates with c.toLowerCase() === b.toLowerCase()
      * @param candidates
      * @param b
      */
      static sEquals(candidates: string[], b: string): boolean {
        b = b.toLowerCase();
        // candidates = candidates.map((s: String) => s.toLowerCase());
        for (let s of candidates) {
          if (s && (s.toLowerCase() === b)) {
            return true;
          }
        }
        return false;
      }

      static isSimpleQuery(query: string) {
        return !/^\S+\s+\S+$/.exec(query) && !/and|or|\(|\)|\!|\[|\]|"|'/.exec(query);
      }

      /**
       * Returns number of times character in b appears in s
       * @param s
       * @param b is assumed to have b.length ===1
       */
      static occurences(s: string, b: string) {
        return s.split(b).length - 1;
      }

      /**
       * Function that 'drives' query parsing
       * @param query
       */
      parseFilterQuery(query: string): FilterPredicate {
        let origQuery: string = query;
        // query = query.replace(/\s/g, '').toLowerCase();
        query = query.toLowerCase();
        try {
        let predicate: FilterPredicate = FilterParserService.parseOr(query);
          if (predicate) {
            console.log('Emitting true on succ obj');
            this.parsingSuccessSubject.next(true);
            return predicate;
          } else {
            // parsing expression was unsuce
            if (FilterParserService.isSimpleQuery(origQuery)) {
              this.parsingSuccessSubject.next(true);
              console.log('simple pred');
              return FilterParserService.filterPredicate;
            } else {
              this.parsingSuccessSubject.next(false);
            }
          }
        } catch (e) {
          this.parsingSuccessSubject.next(false);
          this.parsingErrorMsgSubject.next(e.message);
          console.log(e.message);
        }
      }

      /**
       * Set success to true, as this is the initial state
       */
      disconnect(): void {
        this.parsingSuccessSubject.next(true);
        this.parsingErrorMsgSubject.next('');
      }
    }
