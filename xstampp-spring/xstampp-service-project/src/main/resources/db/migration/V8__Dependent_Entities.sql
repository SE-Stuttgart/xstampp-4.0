ALTER TABLE "controller" ADD COLUMN processModel_id uuid;
ALTER TABLE "controller" ADD COLUMN responsibility_id uuid;
ALTER TABLE "controller" ADD COLUMN controlAlgorithm_id uuid;
ALTER TABLE "controlled_process" ADD COLUMN feedback_id uuid;
ALTER TABLE "control_action" ADD COLUMN uca_id uuid;
ALTER TABLE "control_action" ADD COLUMN controller_id uuid;
ALTER TABLE "unsafe_control_action" ADD COLUMN loss_scenario_id uuid;
ALTER TABLE "feedback" ADD COLUMN sensor_id uuid;
ALTER TABLE "input" ADD COLUMN controller_id uuid;
ALTER TABLE "output" ADD COLUMN controlled_process_id uuid;
ALTER TABLE "output" ADD COLUMN controller_id uuid;
ALTER TABLE "sensor" ADD COLUMN control_action_id uuid;
ALTER TABLE "loss_scenario" ADD COLUMN implementation_constraint_id uuid;





