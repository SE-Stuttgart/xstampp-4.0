ALTER TABLE "system_description" ADD COLUMN last_editor_id uuid;
ALTER TABLE "system_description" ADD CONSTRAINT system_description_user_id_fkey
FOREIGN KEY (last_editor_id, last_editor_displayname) REFERENCES "user"(id, displayname) ON DELETE SET NULL;

ALTER TABLE "loss" ADD COLUMN last_editor_id uuid;
ALTER TABLE "loss" ADD CONSTRAINT loss_user_id_fkey
FOREIGN KEY (last_editor_id, last_editor_displayname) REFERENCES "user"(id, displayname) ON DELETE SET NULL;

ALTER TABLE "hazard" ADD COLUMN last_editor_id uuid;
ALTER TABLE "hazard" ADD CONSTRAINT hazard_user_id_fkey
FOREIGN KEY (last_editor_id, last_editor_displayname) REFERENCES "user"(id, displayname) ON DELETE SET NULL;

ALTER TABLE "sub_hazard" ADD COLUMN last_editor_id uuid;
ALTER TABLE "sub_hazard" ADD CONSTRAINT sub_hazard_user_id_fkey
FOREIGN KEY (last_editor_id, last_editor_displayname) REFERENCES "user"(id, displayname) ON DELETE SET NULL;

ALTER TABLE "system_constraint" ADD COLUMN last_editor_id uuid;
ALTER TABLE "system_constraint" ADD CONSTRAINT system_constraint_user_id_fkey
FOREIGN KEY (last_editor_id, last_editor_displayname) REFERENCES "user"(id, displayname) ON DELETE SET NULL;

ALTER TABLE "sub_system_constraint" ADD COLUMN last_editor_id uuid;
ALTER TABLE "sub_system_constraint" ADD CONSTRAINT sub_system_constraint_user_id_fkey
FOREIGN KEY (last_editor_id, last_editor_displayname) REFERENCES "user"(id, displayname) ON DELETE SET NULL;

ALTER TABLE "box" ADD COLUMN last_editor_id uuid;
ALTER TABLE "box" ADD CONSTRAINT box_user_id_fkey
FOREIGN KEY (last_editor_id, last_editor_displayname) REFERENCES "user"(id, displayname) ON DELETE SET NULL;

ALTER TABLE "arrow" ADD COLUMN last_editor_id uuid;
ALTER TABLE "arrow" ADD CONSTRAINT arrow_user_id_fkey
FOREIGN KEY (last_editor_id, last_editor_displayname) REFERENCES "user"(id, displayname) ON DELETE SET NULL;

ALTER TABLE "controller" ADD COLUMN last_editor_id uuid;
ALTER TABLE "controller" ADD CONSTRAINT controller_user_id_fkey
FOREIGN KEY (last_editor_id, last_editor_displayname) REFERENCES "user"(id, displayname) ON DELETE SET NULL;

ALTER TABLE "controlled_process" ADD COLUMN last_editor_id uuid;
ALTER TABLE "controlled_process" ADD CONSTRAINT controlled_process_user_id_fkey
FOREIGN KEY (last_editor_id, last_editor_displayname) REFERENCES "user"(id, displayname) ON DELETE SET NULL;

ALTER TABLE "control_algorithm" ADD COLUMN last_editor_id uuid;
ALTER TABLE "control_algorithm" ADD CONSTRAINT control_algorithm_user_id_fkey
FOREIGN KEY (last_editor_id, last_editor_displayname) REFERENCES "user"(id, displayname) ON DELETE SET NULL;

ALTER TABLE "process_variable" ADD COLUMN last_editor_id uuid;
ALTER TABLE "process_variable" ADD CONSTRAINT process_variable_user_id_fkey
FOREIGN KEY (last_editor_id, last_editor_displayname) REFERENCES "user"(id, displayname) ON DELETE SET NULL;

ALTER TABLE "control_action" ADD COLUMN last_editor_id uuid;
ALTER TABLE "control_action" ADD CONSTRAINT control_action_user_id_fkey
FOREIGN KEY (last_editor_id, last_editor_displayname) REFERENCES "user"(id, displayname) ON DELETE SET NULL;

ALTER TABLE "unsafe_control_action" ADD COLUMN last_editor_id uuid;
ALTER TABLE "unsafe_control_action" ADD CONSTRAINT unsafe_control_action_user_id_fkey
FOREIGN KEY (last_editor_id, last_editor_displayname) REFERENCES "user"(id, displayname) ON DELETE SET NULL;

ALTER TABLE "controller_constraint" ADD COLUMN last_editor_id uuid;
ALTER TABLE "controller_constraint" ADD CONSTRAINT controller_constraint_user_id_fkey
FOREIGN KEY (last_editor_id, last_editor_displayname) REFERENCES "user"(id, displayname) ON DELETE SET NULL;

ALTER TABLE "feedback" ADD COLUMN last_editor_id uuid;
ALTER TABLE "feedback" ADD CONSTRAINT feedback_user_id_fkey
FOREIGN KEY (last_editor_id, last_editor_displayname) REFERENCES "user"(id, displayname) ON DELETE SET NULL;

ALTER TABLE "input" ADD COLUMN last_editor_id uuid;
ALTER TABLE "input" ADD CONSTRAINT input_user_id_fkey
FOREIGN KEY (last_editor_id, last_editor_displayname) REFERENCES "user"(id, displayname) ON DELETE SET NULL;

ALTER TABLE "output" ADD COLUMN last_editor_id uuid;
ALTER TABLE "output" ADD CONSTRAINT output_user_id_fkey
FOREIGN KEY (last_editor_id, last_editor_displayname) REFERENCES "user"(id, displayname) ON DELETE SET NULL;

ALTER TABLE "actuator" ADD COLUMN last_editor_id uuid;
ALTER TABLE "actuator" ADD CONSTRAINT actuator_user_id_fkey
FOREIGN KEY (last_editor_id, last_editor_displayname) REFERENCES "user"(id, displayname) ON DELETE SET NULL;

ALTER TABLE "sensor" ADD COLUMN last_editor_id uuid;
ALTER TABLE "sensor" ADD CONSTRAINT sensor_user_id_fkey
FOREIGN KEY (last_editor_id, last_editor_displayname) REFERENCES "user"(id, displayname) ON DELETE SET NULL;

ALTER TABLE "responsibility" ADD COLUMN last_editor_id uuid;
ALTER TABLE "responsibility" ADD CONSTRAINT responsibility_user_id_fkey
FOREIGN KEY (last_editor_id, last_editor_displayname) REFERENCES "user"(id, displayname) ON DELETE SET NULL;

ALTER TABLE "loss_scenario" ADD COLUMN last_editor_id uuid;
ALTER TABLE "loss_scenario" ADD CONSTRAINT loss_scenario_user_id_fkey
FOREIGN KEY (last_editor_id, last_editor_displayname) REFERENCES "user"(id, displayname) ON DELETE SET NULL;

ALTER TABLE "process_model" ADD COLUMN last_editor_id uuid;
ALTER TABLE "process_model" ADD CONSTRAINT process_model_user_id_fkey
FOREIGN KEY (last_editor_id, last_editor_displayname) REFERENCES "user"(id, displayname) ON DELETE SET NULL;

ALTER TABLE "rule" ADD COLUMN last_editor_id uuid;
ALTER TABLE "rule" ADD CONSTRAINT rule_user_id_fkey
FOREIGN KEY (last_editor_id, last_editor_displayname) REFERENCES "user"(id, displayname) ON DELETE SET NULL;

ALTER TABLE "conversion" ADD COLUMN last_editor_id uuid;
ALTER TABLE "conversion" ADD CONSTRAINT conversion_user_id_fkey
FOREIGN KEY (last_editor_id, last_editor_displayname) REFERENCES "user"(id, displayname) ON DELETE SET NULL;

ALTER TABLE "implementation_constraint" ADD COLUMN last_editor_id uuid;
ALTER TABLE "implementation_constraint" ADD CONSTRAINT implementation_constraint_user_id_fkey
FOREIGN KEY (last_editor_id, last_editor_displayname) REFERENCES "user"(id, displayname) ON DELETE SET NULL;

