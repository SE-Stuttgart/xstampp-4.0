import { TestBed, async } from '@angular/core/testing';

import { FilterParserService } from './filter-parser.service';
import { MainTableEntity } from 'src/app/types/local-types';
const sensorElement = { name: 'Sensor critically damaged', description:'If the sensor is exposed to increased temperature of a...'};
const anotherElement: MainTableEntity = { id: '5', name: 'I dont really care', description: 'This is another description on a0d23 ??!  the job',
subSafetyConstraintDisplayId: '1.2', lastEdited: new Date().getTime(), lastEditor: 'Bert Breakdance', state: 'TODO' };
describe('FilterParserService', () => {
  let service: FilterParserService;
  let valueQuerys = {
    stringLit: "the sensor is exposed",
    num: "1.3",
    interval: "[1, 1]",
  };
  beforeEach( () => {
    service = TestBed.get(FilterParserService);
  })
  it('should be created', () => {
    expect(service).toBeTruthy();
  });
  it('singlestring literal should match element', () => {
    expect(service.parseFilterQuery('"sensor"')(sensorElement, '')).toBe(true);
  });

  it('singlestring literal should not match element', () => {
    expect(service.parseFilterQuery('"sensor"')(anotherElement, '')).toBe(false);
  });

  it('multiword string literal should match element', () => {
    expect(service.parseFilterQuery('"the sensor is exposed"')(sensorElement, '')).toBe(true);
  }); 

  it('and has higher precedence than or', () => {
    expect(service.parseFilterQuery('"If the sensor" or "_" and "_"')(sensorElement, '')).toBe(true);
  });

  it('and has higher precedence than or', () => {
    expect(service.parseFilterQuery('"sensor" and "exposed" or "_" and "_"')(sensorElement, '')).toBe(true);
  });

  it('empty string literal doesn\t match', () => {
    expect(service.parseFilterQuery('""')(sensorElement, '')).toBe(false);
  });

  it("one or, one nester or", () => {
    expect(service.parseFilterQuery('"sensor" or ("a" or "b")')(sensorElement, '')).toBe(true);
  });

  it("nested curly braces", () => {
    expect(service.parseFilterQuery('"_" or ("sensor" or ("_" or "_"))')(sensorElement, '')).toBe(true);
  });

  it("element in interval", () => {
    expect(service.parseFilterQuery('  [4, 7]   ')(anotherElement, '')).toBe(true);
  });

});
