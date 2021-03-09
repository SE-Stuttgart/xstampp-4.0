import {
  AfterContentInit,
  ContentChildren,
  Directive,
  Input,
  OnChanges,
  QueryList,
  SimpleChanges,
} from '@angular/core';
import { RouterLinkActive } from '@angular/router';

@Directive({
  // (unsave) hacky overload
  // tslint:disable-next-line: directive-selector
  selector: '[routerLink]',
})

/**
 * TODO:
 * FIXME:
 * hacky workaround for routerDetection BUG in angular.
 * Can be deleted when Angular Ticket:
 * https://github.com/angular/angular/issues/18469
 * is closed and Project is updated to new Angular version.
 *
 * BUG:
 * When an url is loaded in the sub-app, the side-nav is not updated
 */
export class RouterLinkDirective implements OnChanges, AfterContentInit {
  @Input() routerLink: any[] | string;

  @ContentChildren(RouterLinkActive, { descendants: true })
  linkActives !: QueryList<RouterLinkActive>;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.routerLink && this.linkActives && this.linkActives.first) {
      this.linkActives.first.ngOnChanges(null);
    }
  }

  ngAfterContentInit(): void {
    this.linkActives.changes.subscribe(() => this.linkActives.first.ngOnChanges(null));
  }
}
