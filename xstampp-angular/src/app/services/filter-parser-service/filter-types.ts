export type FilterPredicate = (obj: any, filter: string) => boolean;

export enum Values {
    StringLiteral = "StringLiteral",
    Number = "Number",
    EntityState = "EntityState",
    Interval = "Interval",
    Regex = "Regex",
    Recent = "Recent",
    LastDays = "LastDays",
}
export type ValueKind= "SL"|"N";



export type ValuesRegex = Record<string, RegExp>;
