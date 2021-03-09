CREATE TABLE "loss_scenario"
(
    id integer,
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    uca_id integer,

    name character varying (128),
    description text,
    
    head_category integer,
    sub_category integer,
    controller1_id integer,
    controller2_id integer,
    control_algorithm integer,
    description1 text,
    description2 text,
    description3 text,
    control_action_id integer,
    input_arrow_id integer,
    feedback_arrow_id integer,
    input_box_id text,
    sensor_id integer,
    reason integer,

    last_edited timestamp (3) with time zone,
    last_editor_displayname character varying(128),
    lock_holder_id uuid,
    lock_holder_displayname character varying(128),
    lock_exp_time timestamp (3) with time zone,

    FOREIGN KEY (controller1_id, project_id) REFERENCES "controller" (id, project_id) ON DELETE SET NULL,
    FOREIGN KEY (controller2_id, project_id) REFERENCES "controller" (id, project_id) ON DELETE SET NULL,
    FOREIGN KEY (control_action_id, project_id) REFERENCES "control_action" (id, project_id) ON DELETE SET NULL,
    -- FOREIGN KEY (input_arrow_id, project_id) REFERENCES "arrow" (id, project_id) ON DELETE SET NULL,
    -- FOREIGN KEY (feedback_arrow_id, project_id) REFERENCES "arrow" (id, project_id) ON DELETE SET NULL,
    -- FOREIGN KEY (input_box_id, project_id) REFERENCES "box" (id, project_id) ON DELETE SET NULL,
    FOREIGN KEY (sensor_id, project_id) REFERENCES "sensor" (id, project_id) ON DELETE SET NULL,
    PRIMARY KEY(id,project_id)
);