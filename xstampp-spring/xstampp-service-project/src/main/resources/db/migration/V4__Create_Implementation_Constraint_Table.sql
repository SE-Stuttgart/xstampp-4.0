CREATE TABLE "implementation_constraint"
(
    id integer,
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    loss_scenario_id integer,

    name character varying (128),
    description text,

    last_edited timestamp (3) with time zone,
    last_editor_displayname character varying(128),
    lock_holder_id uuid,
    lock_holder_displayname character varying(128),
    lock_exp_time timestamp (3) with time zone,

    FOREIGN KEY (loss_scenario_id, project_id) REFERENCES "loss_scenario" (id, project_id) ON DELETE CASCADE,
    PRIMARY KEY(id,project_id)
);