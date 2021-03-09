
CREATE TABLE "system_constraint_responsibility_link"
(
    responsibility_id integer NOT NULL,
    system_constraint_id integer NOT NULL,
    project_id uuid,

    FOREIGN KEY (responsibility_id, project_id) REFERENCES "responsibility" (id, project_id) ON DELETE CASCADE,
    FOREIGN KEY (system_constraint_id, project_id) REFERENCES "system_constraint" (id, project_id) ON DELETE CASCADE,
    PRIMARY KEY (responsibility_id, system_constraint_id, project_id)
);
