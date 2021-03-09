CREATE TABLE "theme"
(
    id integer NOT NULL,
    name character varying(128),
    colors text,

    
    PRIMARY KEY (id)
);

CREATE TABLE "icon"
(
    id integer NOT NULL,
    name character varying(128),
    image bytea,
    
    PRIMARY KEY (id)
);


ALTER TABLE "user"
ADD COLUMN theme integer,
ADD COLUMN icon text,
ADD FOREIGN KEY (theme) REFERENCES "theme" (id) ON DELETE SET NULL;