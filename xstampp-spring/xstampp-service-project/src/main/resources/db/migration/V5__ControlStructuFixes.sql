ALTER TABLE "arrow" ALTER COLUMN label TYPE character varying (512);

ALTER TABLE "box"
ALTER COLUMN parent_id TYPE integer USING (parent_id::integer),
DROP CONSTRAINT "box_parent_id_fkey";

ALTER TABLE "arrow"
ALTER COLUMN parent_id TYPE character varying(512);
ALTER TABLE "process_variable" DROP CONSTRAINT "process_variable_arrow_id_fkey";