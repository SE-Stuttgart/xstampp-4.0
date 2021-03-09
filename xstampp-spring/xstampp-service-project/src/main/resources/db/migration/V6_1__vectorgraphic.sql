
CREATE TABLE "vectorgraphic"
(
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    graphic text,
    has_colour boolean,

    PRIMARY KEY (project_id, has_colour)
);
