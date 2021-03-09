-- Drop First Versions of Control Algorithm Tables (never used)
DROP TABLE "rule_control_action_link" RESTRICT;
DROP TABLE "rule" RESTRICT;

-- Create New Control Algorithm Tables
CREATE TABLE "rule"
(
    id integer NOT NULL,
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    parent_id integer,
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

CREATE TABLE "rule_last_id"
(
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    controller_id integer NOT NULL,

    last_id integer NOT NULL,

    FOREIGN KEY (controller_id, project_id) REFERENCES "controller" (id, project_id) ON DELETE CASCADE
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

CREATE TABLE "conversion"
(
    id integer NOT NULL,
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    parent_id integer,
    control_action_id integer,
    actuator_id integer,
    conversion text,

    last_edited timestamp (3) with time zone,
    last_editor_displayname character varying(128),
    lock_holder_id uuid,
    lock_holder_displayname character varying(128),
    lock_exp_time timestamp (3) with time zone,

    PRIMARY KEY (id, actuator_id, project_id)
);

CREATE TABLE "conversion_last_id"
(
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    actuator_id integer NOT NULL,

    last_id integer NOT NULL,

    FOREIGN KEY (actuator_id, project_id) REFERENCES "actuator" (id, project_id) ON DELETE CASCADE
);