import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class RuleParserService {
  constructor() { }

  private title: string = '';
  private functions: string = '';
  private xNames: string = '';
  private yName: string = '';
  private variables: string[] = [];
  private rules: string[] = [];

  /**
  /* gets a trimmed version of the function literal that can be used as the graph title
  /*
  /* @return a string containing the title
   */
  public getTitle(): string {
    return this.title;
  }

  /**
  /* gets all functions that need to be plotted
  /*
  /* @return a string containing all functions separated by '}, {fn: ',
  /* or '0' if there aren't any valid ones
   */
  public getFunctions(): string {
    return this.functions;
  }

  /**
  /* gets the names of the x-variables
  /*
  /* @return a string containing all x-variables separated by ', ',
  /* an empty string if there are none,
  /* or an error message if there is more than one in each equation part
   */
  public getXNames(): string {
    return this.xNames;
  }

  /**
  /* gets the name of the y-variable
  /*
  /* @return a string containing the y-variable,
  /* or an error message if there is none
   */
  public getYName(): string {
    return this.yName;
  }

  /**
  /* gets a list of all variable names inside the function, NOT including the y-variable
  /*
  /* @return a list of strings which represent the variable names.
  /* can be empty.
   */
  public getVariables(): string[] {
    return this.variables;
  }

  /**
  /* (Re)initializes the service with a new rule.
  /* Afterwards, all getters will return the values corresponding to that rule.
  /* Use update() to change which variables are used as x-variable
  /* and which have a fixed value.
  /*
  /* @param rule: the rule to be used as basis
  /* @param yName: the default name for y, in case the rule has no (un)equations
   */
  public init(rule: string, yName: string): void {
    // reset data
    this.title = '';
    this.xNames = '';
    this.yName = yName;
    this.variables = [];
    this.rules = [];

    // remove linebreaks and @
    rule = rule.replace(/\r?\n|\r/g, '');
    rule = rule.split('@').join('');

    // extract part between square brackets
    let ruleParts = [];
    let originalRuleParts = [];
    if (rule.indexOf('[') !== -1) {
      rule = rule.replace(/[^\[]*\[?(<content>[^\]]*)\].*/, '$<content>'); // FIXME: was like that: /[^\[]*\[(?<content>[^\]]*)\].*/ is that how the Tobias thought?
      // split the result at =, <, >, etc.
      let allRuleParts = rule.split(/(=|>|<|!)/);
      // tslint:disable-next-line: forin
      for (let i in allRuleParts) {
        // remove whitespaces and function names
        let str = allRuleParts[i].split(' ').join('');
        // only keep relevant strings
        if (str !== '' && str !== '!' && str !== '=' && str !== '<' && str !== '>') {
          originalRuleParts.push(str);
          ruleParts.push(str.replace(/[a-zA-Z]\w*\(/g, '('));
          // extract variable names
          let newVariables = str.match(/[a-zA-Z]\w*/g);
          if (newVariables == null) { newVariables = []; }
          newVariables = newVariables.filter((value: string) => value !== 'pi' && value !== 'e');
          this.variables = this.variables.concat(newVariables);
        }
      }
      // determine y
      let index = -1;
      for (let i = ruleParts.length - 1; i >= 0; --i) {
        if (ruleParts[i].replace(/[a-zA-Z]\w*/, '').length === 0
          && this.variables.filter((value: string) => value === ruleParts[i]).length === 1) {
          index = i;
        }
      }
      if (index > -1) {
        this.yName = ruleParts[index];
        originalRuleParts = originalRuleParts.filter((value: string) => value !== this.yName);
        this.variables = this.variables.filter((value: string) => value !== this.yName);
      } else {
        this.yName = 'unable to determine y';
        this.functions = '0';
      }
      ruleParts = originalRuleParts;
    } else {
      // remove whitespaces and function names, then extract variable names
      ruleParts.push(rule.split(' ').join(''));
      this.variables = ruleParts[0].replace(/[a-zA-Z]\w*\(/g, '(').match(/[a-zA-Z]\w*/g);
      if (this.variables == null) { this.variables = []; }
      this.variables = this.variables.filter((value: string) => value !== 'pi' && value !== 'e');
    }

    // set title
    if (rule.length > 34) {
      this.title = rule.slice(0, 32) + '...';
    } else {
      this.title = rule;
    }

    // if erroneous no need to process further
    if (this.functions === '0') {
      this.variables = [];
      return;
    }

    // store variable names
    let unique = new Set(this.variables);
    this.variables = Array.from(unique);

    // store functions
    this.rules = ruleParts;

    // set default values ('x' for the first variable, '0' for the rest)
    let defaultValues = Array.from('0'.repeat(this.variables.length));
    if (defaultValues.length > 0) {
      defaultValues[0] = 'x';
    }
    this.update(defaultValues);
  }

  /**
  /* updates the values of the variables inside the rule
  /*
  /* @param values: an array containing the values to be assigned to the variables.
  /* should be either 'x' or a number
  /* array length and value order should match the variables array
   */
  public update(values: string[]): void {

    this.functions = '';
    this.xNames = '';
    // replace variable names in rules
    let replacedRules = Array.from(this.rules);
    for (let i = 0; i < this.variables.length; i++) {
      let expression = new RegExp('(|\\+|\\-|\\*|\\\/|\\^|\\!|^)' + this.variables[i] + '(|\\+|\\-|\\*|\\\/|\\^|\\!|$)', 'g');
      for (let j = 0; j < replacedRules.length; j++) {
        replacedRules[j] = replacedRules[j].replace(expression, '$1' + values[i] + '$2');
        replacedRules[j] = replacedRules[j].replace(expression, '$1' + values[i] + '$2');
      }
      // set x value names
      if (values[i] === 'x') {
        this.xNames = this.xNames + this.variables[i] + ', ';
      }
    }
    this.xNames = this.xNames.slice(0, -2);
    // set functions
    for (let i = 0; i < replacedRules.length; i++) {
      this.functions = this.functions + replacedRules[i] + '\' }, { fn: \'';
    }
    this.functions = this.functions.slice(0, -12);
  }

  /**
  /* checks whether the syntax of a rule is correct
  /*
  /* @param rule: the rule to be checked
  /* @return an empty string, iff the syntax is correct;
  /* an error message otherwise
   */
  public checkSyntax(rule: string): string {
    rule = rule.split('@').join('');
    rule = rule.replace(/\r?\n|\r/g, ' ');
    try {
      let parser = new nearley.Parser(grammar.ParserRules, grammar.ParserStart);
      parser.feed(rule);
      let result = parser.results[0];
      if (result == undefined) {
        return 'Something is missing at the end of the input.\nPlease make sure you closed all brackets.';
      }
    } catch (err) {
      // incorrect syntax
      return err.message;
    }
    return '';
  }
}

declare const nearley: any;
declare const grammar: any;
