
CREATE TABLE "rule"
(
    id integer NOT NULL,
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    control_action_id integer,
    controller_id integer,
    rule text,

    last_edited timestamp (3) with time zone,
    last_editor_displayname character varying(128),
    lock_holder_id uuid,
    lock_holder_displayname character varying(128),
    lock_exp_time timestamp (3) with time zone,

    PRIMARY KEY (id, controller_id, project_id)
);

CREATE TABLE "rule_control_action_link"
(
    rule_id integer NOT NULL,
    control_action_id integer NOT NULL,
    project_id uuid,
    controller_id integer,

    FOREIGN KEY (rule_id, controller_id, project_id) REFERENCES "rule" (id, controller_id, project_id) ON DELETE CASCADE,
    FOREIGN KEY (control_action_id, project_id) REFERENCES "control_action" (id, project_id) ON DELETE CASCADE,
    PRIMARY KEY (rule_id, control_action_id, project_id)
);
