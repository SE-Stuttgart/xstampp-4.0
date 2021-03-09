CREATE TABLE "process_model"
(
    id integer,
    project_id uuid REFERENCES "project"(id) ON DELETE CASCADE,
    name character varying(128),
    description text,
    controller_id integer,

    last_edited timestamp (3)
    with time zone,
    last_editor_displayname character varying
    (128),
    lock_holder_id uuid,
    lock_holder_displayname character varying
    (128),
    lock_exp_time timestamp
    (3)
    with time zone,

    PRIMARY KEY
    (project_id, id)

);

    CREATE TABLE "process_model_process_variable_link"
    (
        process_model_id integer NOT NULL,
        process_variable_id integer NOT NULL,
        project_id uuid,
        process_variable_value text,

        FOREIGN KEY (process_model_id, project_id) REFERENCES "process_model" (id, project_id) ON DELETE CASCADE,
        FOREIGN KEY (process_variable_id, project_id) REFERENCES "process_variable" (id, project_id) ON DELETE CASCADE,
        PRIMARY KEY (process_model_id, process_variable_id, project_id)
    );

    CREATE TABLE "process_variable_responsibility_link"
    (
        process_variable_id integer NOT NULL,
        responsibility_id integer NOT NULL,
        project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,

        FOREIGN KEY (process_variable_id, project_id) REFERENCES "process_variable" (id, project_id) ON DELETE CASCADE,
        FOREIGN KEY (responsibility_id, project_id) REFERENCES "responsibility" (id, project_id) ON DELETE CASCADE,
        PRIMARY KEY (process_variable_id, responsibility_id, project_id)
    );

    CREATE TABLE "discrete_process_variable_values"
    (
        project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
        process_variable_id integer NOT NULL ,
        variable_value varchar(128) NOT NULL ,

        FOREIGN KEY (process_variable_id, project_id) REFERENCES "process_variable" (id, project_id) ON DELETE CASCADE,
        PRIMARY KEY (project_id, process_variable_id,variable_value)
    );
ALTER TABLE "process_variable" DROP box_id;
--DROP UNIQUE;
ALTER TABLE "process_variable" ADD COLUMN arrow_id character varying (128), ADD COLUMN variable_type character varying (128);
ALTER TABLE "process_variable"  ADD FOREIGN KEY (arrow_id,project_id) REFERENCES "arrow" (id,project_id) ON DELETE CASCADE;


-- To show differences in the process_variable table here are the complete tables of the "old" and "new" version
--NEW
--CREATE TABLE "process_variable"
--(
  --  id integer,
   -- project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
   -- name character varying (128),
   -- description text,
   -- arrow_id character varying (128),
   -- variable_type character varying (128),
    
   -- last_edited timestamp (3) with time zone,
   -- last_editor_displayname character varying(128),
  --  lock_holder_id uuid,
   -- lock_holder_displayname character varying(128),
  --  lock_exp_time timestamp (3) with time zone,
    
  --  PRIMARY KEY (id,project_id)
--);

--OLD
--CREATE TABLE "process_variable"
--(
 --   id integer,
  --  project_id uuid REFERENCES "project" (id) ON DELETE CASCADE,
 --   name character varying (128),
  --  description text,
  --  box_id character varying (128),

   -- last_edited timestamp (3) with time zone,
   -- last_editor_displayname character varying(128),
   -- lock_holder_id uuid,
   -- lock_holder_displayname character varying(128),
   -- lock_exp_time timestamp (3) with time zone,

  --  UNIQUE (project_id, box_id),
  --  PRIMARY KEY (id,project_id)
--);
