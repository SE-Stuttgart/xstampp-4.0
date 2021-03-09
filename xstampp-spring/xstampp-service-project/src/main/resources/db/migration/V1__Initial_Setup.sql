
-- this project table is weakly linked to the separate master database
-- see XSTAMPP 4.0 architecture concept for details and reasoning

CREATE TABLE "project"
(
    id uuid NOT NULL,
	
	lock_holder_id uuid,
    lock_holder_displayname character varying(128),
    lock_exp_time timestamp with time zone,

    PRIMARY KEY (id)
);

--We differentiate between three different types of entities.
--1. The project entity which is defined by it's UUID
--2. The project dependent entities which are defined by it's ID
	-- and it's corresponding projectId
--3. The Entity dependent entities which are defined by it's ID,
	-- it's corresponding parentId and it's corresponding  projectId.
	

CREATE TABLE "system_description"
(
    id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    description text,

    last_edited timestamp (3) with time zone,
    last_editor_displayname character varying(128),
    lock_holder_id uuid,
    lock_holder_displayname character varying(128),
    lock_exp_time timestamp (3) with time zone,
    
    PRIMARY KEY (id)
);

CREATE TABLE "loss"
(
    id integer NOT NULL,
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    name character varying(128),
    description text,

    last_edited timestamp (3) with time zone,
    last_editor_displayname character varying(128),
    lock_holder_id uuid,
    lock_holder_displayname character varying(128),
    lock_exp_time timestamp (3) with time zone,

    PRIMARY KEY (id, project_id)
);

CREATE TABLE "hazard"
(
    id integer NOT NULL,
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    name character varying(128),
    description text,

    last_edited timestamp (3) with time zone,
    last_editor_displayname character varying(128),
    lock_holder_id uuid,
    lock_holder_displayname character varying(128),
    lock_exp_time timestamp (3) with time zone,

    PRIMARY KEY (id, project_id)
);

CREATE TABLE "hazard_loss_link"
(
    hazard_id integer NOT NULL,
    loss_id integer NOT NULL,
    project_id uuid,

    FOREIGN KEY (hazard_id, project_id) REFERENCES "hazard" (id, project_id) ON DELETE CASCADE,
    FOREIGN KEY (loss_id, project_id) REFERENCES "loss" (id, project_id) ON DELETE CASCADE,
    PRIMARY KEY (hazard_id, loss_id, project_id)
);

CREATE TABLE "sub_hazard"
(
    id integer,
    project_id uuid,
    name character varying (128),
    description text,
    parent_id integer,

    last_edited timestamp (3) with time zone,
    last_editor_displayname character varying(128),
    lock_holder_id uuid,
    lock_holder_displayname character varying(128),
    lock_exp_time timestamp (3) with time zone,

    FOREIGN KEY (parent_id, project_id) REFERENCES "hazard" (id, project_id) ON DELETE CASCADE,
    PRIMARY KEY (id, parent_id, project_id)
);

CREATE TABLE "system_constraint"
(
    id integer NOT NULL,
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    name character varying (128),
    description text,
    
    last_edited timestamp (3) with time zone,
    last_editor_displayname character varying(128),
    lock_holder_id uuid,
    lock_holder_displayname character varying(128),
    lock_exp_time timestamp (3) with time zone,

    PRIMARY KEY (id, project_id)
);

CREATE TABLE "system_constraint_hazard_link"
(
    hazard_id integer NOT NULL,
    system_constraint_id integer NOT NULL,
    project_id uuid,

    FOREIGN KEY (hazard_id, project_id) REFERENCES "hazard" (id, project_id) ON DELETE CASCADE,
    FOREIGN KEY (system_constraint_id, project_id) REFERENCES "system_constraint" (id, project_id) ON DELETE CASCADE,
    PRIMARY KEY (hazard_id, system_constraint_id, project_id)
);

CREATE TABLE "sub_system_constraint"
(
    id integer NOT NULL,
    project_id uuid,
    parent_id integer,
    sub_hazard_project_id uuid,
    sub_hazard_id integer,
    hazard_id integer,
    name character varying (128),
    description text,
    
    last_edited timestamp (3) with time zone,
    last_editor_displayname character varying(128),
    lock_holder_id uuid,
    lock_holder_displayname character varying(128),
    lock_exp_time timestamp (3) with time zone,
    

    
    FOREIGN KEY (parent_id, project_id) REFERENCES "system_constraint" (id, project_id) ON DELETE CASCADE,
    FOREIGN KEY (hazard_id, sub_hazard_id, sub_hazard_project_id) REFERENCES "sub_hazard" (parent_id, id, project_id) ON DELETE SET NULL,
    PRIMARY KEY (id, parent_id, project_id),

    CHECK (project_id = sub_hazard_project_id OR sub_hazard_project_id IS NULL)
);

CREATE TABLE "box"
(
    id character varying (128),
    name character varying (128),
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    box_type character varying(128),
    x integer,
    y integer,
    height integer,
    width integer,
    parent_id character varying (128),
    
    last_edited timestamp (3) with time zone,
    last_editor_displayname character varying(128),
    
    FOREIGN KEY (parent_id, project_id) REFERENCES "box" (id, project_id),
    PRIMARY KEY (id, project_id)
);

CREATE TABLE "arrow"
(
    id character varying (128),
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    source character varying (128),
    destination character varying (128),
    label character varying (128),
    arrow_type character varying(128),
    parent_id character varying (128),
    parts integer [],
    
    last_edited timestamp (3) with time zone,
    last_editor_displayname character varying(128),
    
    PRIMARY KEY (id, project_id)
);

CREATE TABLE "controller"
(
    id integer,
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    name character varying (128),
    description text,
    box_id character varying (128),

    last_edited timestamp (3) with time zone,
    last_editor_displayname character varying(128),
    lock_holder_id uuid,
    lock_holder_displayname character varying(128),
    lock_exp_time timestamp (3) with time zone,

    UNIQUE (project_id, box_id),
    PRIMARY KEY (id,project_id)
);

CREATE TABLE "controlled_process"
(
    id integer,
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    name character varying (128),
    description text,
    box_id character varying (128),

    last_edited timestamp (3) with time zone,
    last_editor_displayname character varying(128),
    lock_holder_id uuid,
    lock_holder_displayname character varying(128),
    lock_exp_time timestamp (3) with time zone,

    UNIQUE (project_id, box_id),
    PRIMARY KEY (id,project_id)
);

CREATE TABLE "control_algorithm"
(
    id integer,
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    name character varying (128),
    description text,
    box_id character varying (128),

    last_edited timestamp (3) with time zone,
    last_editor_displayname character varying(128),
    lock_holder_id uuid,
    lock_holder_displayname character varying(128),
    lock_exp_time timestamp (3) with time zone,

    UNIQUE (project_id, box_id),
    PRIMARY KEY (id,project_id)
);

CREATE TABLE "process_variable"
(
    id integer,
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    name character varying (128),
    description text,
    box_id character varying (128),

    last_edited timestamp (3) with time zone,
    last_editor_displayname character varying(128),
    lock_holder_id uuid,
    lock_holder_displayname character varying(128),
    lock_exp_time timestamp (3) with time zone,

    UNIQUE (project_id, box_id),
    PRIMARY KEY (id,project_id)
);


CREATE TABLE "control_action"
(
    id integer,
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    name character varying (128),
    description text,
    arrow_id character varying (128),

    last_edited timestamp (3) with time zone,
    last_editor_displayname character varying(128),
    lock_holder_id uuid,
    lock_holder_displayname character varying(128),
    lock_exp_time timestamp (3) with time zone,

    PRIMARY KEY (id,project_id)
);

CREATE TABLE "unsafe_control_action"
(
    id integer NOT NULL,
    project_id uuid,
    name character varying (128),
    description text,
    category character varying (64),
    parent_id integer,
    
    last_edited timestamp (3) with time zone,
    last_editor_displayname character varying(128),
    lock_holder_id uuid,
    lock_holder_displayname character varying(128),
    lock_exp_time timestamp (3) with time zone,

    FOREIGN KEY (parent_id, project_id) REFERENCES "control_action" (id, project_id) ON DELETE CASCADE,
    PRIMARY KEY (id, parent_id, project_id)
);

CREATE TABLE "controller_constraint"
(
    id integer, --Same as uca id
    project_id uuid,
    parent_id integer, --control_action_id
    name character varying (128),
    description text,
    
    last_edited timestamp (3) with time zone,
    last_editor_displayname character varying(128),
    lock_holder_id uuid,
    lock_holder_displayname character varying(128),
    lock_exp_time timestamp (3) with time zone,
    

    FOREIGN KEY (id, parent_id, project_id) REFERENCES "unsafe_control_action" (id, parent_id, project_id) ON DELETE CASCADE,
    PRIMARY KEY (id, parent_id, project_id)
);

CREATE TABLE "unsafe_control_action_hazard_link" 
(
    control_action_id integer NOT NULL,
    unsafe_control_action_id integer NOT NULL,
    hazard_id integer NOT NULL,
    project_id uuid NOT NULL,
    
    FOREIGN KEY (unsafe_control_action_id, control_action_id, project_id) REFERENCES "unsafe_control_action" (id,  parent_id, project_id) ON DELETE CASCADE,
    FOREIGN KEY (hazard_id, project_id) REFERENCES "hazard" (id, project_id) ON DELETE CASCADE,
    PRIMARY KEY (unsafe_control_action_id, control_action_id, hazard_id, project_id)
);

CREATE TABLE "unsafe_control_action_sub_hazard_link" 
(
    control_action_id integer NOT NULL,
    unsafe_control_action_id integer NOT NULL,
    sub_hazard_id integer NOT NULL,
    hazard_id integer NOT NULL,
    project_id uuid NOT NULL,
    
    FOREIGN KEY (unsafe_control_action_id, control_action_id, project_id) REFERENCES "unsafe_control_action" (id,  parent_id, project_id) ON DELETE CASCADE,
    FOREIGN KEY (sub_hazard_id, hazard_id, project_id) REFERENCES "sub_hazard" (id, parent_id, project_id) ON DELETE CASCADE,
    PRIMARY KEY (unsafe_control_action_id, control_action_id, sub_hazard_id, hazard_id, project_id)
);

CREATE TABLE "feedback"
(
    id integer,
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    process_variable_id integer,
    name character varying (128),
    description text,
    arrow_id character varying (128),

    last_edited timestamp (3) with time zone,
    last_editor_displayname character varying(128),
    lock_holder_id uuid,
    lock_holder_displayname character varying(128),
    lock_exp_time timestamp (3) with time zone,

    FOREIGN KEY (process_variable_id,project_id) REFERENCES "process_variable" (id,project_id),
    PRIMARY KEY (id,project_id) 
);

CREATE TABLE "input"
(
    id integer,
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    name character varying (128),
    description text,
    arrow_id character varying(128),

    last_edited timestamp (3) with time zone,
    last_editor_displayname character varying(128),
    lock_holder_id uuid,
    lock_holder_displayname character varying(128),
    lock_exp_time timestamp (3) with time zone,

    PRIMARY KEY (id, project_id)
);

CREATE TABLE "output"
(
    id integer,
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    name character varying (128),
    description text,
    arrow_id character varying (128),

    last_edited timestamp (3) with time zone,
    last_editor_displayname character varying(128),
    lock_holder_id uuid,
    lock_holder_displayname character varying(128),
    lock_exp_time timestamp (3) with time zone,

    PRIMARY KEY (id, project_id)
);

CREATE TABLE "actuator"
(
    id integer,
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    name character varying (128),
    description text,
    box_id character varying (128),

    last_edited timestamp (3) with time zone,
    last_editor_displayname character varying(128),
    lock_holder_id uuid,
    lock_holder_displayname character varying(128),
    lock_exp_time timestamp (3) with time zone,

    UNIQUE (project_id, box_id),
    PRIMARY KEY (id,project_id)
);

CREATE TABLE "sensor"
(
    id integer,
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    name character varying (128),
    description text,
    box_id character varying (128),

    last_edited timestamp (3) with time zone,
    last_editor_displayname character varying(128),
    lock_holder_id uuid,
    lock_holder_displayname character varying(128),
    lock_exp_time timestamp (3) with time zone,

    UNIQUE (project_id, box_id),
    PRIMARY KEY (id,project_id)
);

CREATE TABLE "project_entity_last_id"
(
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    entity character varying(64) NOT NULL,

    last_id integer NOT NULL
);

CREATE TABLE "sub_hazard_last_id"
(
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    hazard_id integer NOT NULL,

    last_id integer NOT NULL,

    FOREIGN KEY (hazard_id, project_id) REFERENCES "hazard" (id, project_id) ON DELETE CASCADE
);

CREATE TABLE "sub_system_constraint_last_id"
(
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    system_constraint_id integer NOT NULL,

    last_id integer NOT NULL,

    FOREIGN KEY (system_constraint_id, project_id) REFERENCES "system_constraint" (id, project_id) ON DELETE CASCADE
);
CREATE TABLE "unsafe_control_action_last_id"
(
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    control_action_id integer NOT NULL,

    last_id integer NOT NULL,

    FOREIGN KEY (control_action_id, project_id) REFERENCES "control_action" (id, project_id) ON DELETE CASCADE
);

CREATE TABLE "responsibility"
(
    id integer,
    project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
    controller_project_id uuid,
    controller_id integer,
    name character varying (128),
    description text,

    last_edited timestamp (3) with time zone,
    last_editor_displayname character varying(128),
    lock_holder_id uuid,
    lock_holder_displayname character varying(128),
    lock_exp_time timestamp (3) with time zone,
    
    FOREIGN KEY (controller_id, controller_project_id) REFERENCES "controller" (id, project_id) MATCH FULL ON DELETE SET NULL,
    PRIMARY KEY (id,project_id),

    CHECK (project_id = controller_project_id OR controller_project_id IS NULL)
);
