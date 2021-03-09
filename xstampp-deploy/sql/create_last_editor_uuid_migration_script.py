"""
tables list was obtained by command
select t.table_schema,
       t.table_name
from information_schema.tables t
inner join information_schema.columns c on c.table_name = t.table_name 
                                and c.table_schema = t.table_schema
where c.column_name = 'last_edited'
      and t.table_schema not in ('information_schema', 'pg_catalog')
      and t.table_type = 'BASE TABLE'
order by t.table_schema;

"""
mg_script_fname="V5_1__ADD_LAST_EDITOR_ID.sql"
tables = ["system_description", "loss", "hazard",
"sub_hazard",
"system_constraint",
"sub_system_constraint",
"box",
"arrow",
"controller",
"controlled_process",
"control_algorithm",
"process_variable",
"control_action",
"unsafe_control_action",
"controller_constraint",
"feedback",
"input",
"output",
"actuator",
"sensor",
"responsibility",
"loss_scenario",
"process_model",
"rule",
"conversion",
"implementation_constraint"]

table_sql="""ALTER TABLE "{table_name}" ADD COLUMN last_editor_id uuid;
ALTER TABLE "{table_name}" ADD CONSTRAINT {table_name}_user_id_fkey
FOREIGN KEY (last_editor_id, last_editor_displayname) REFERENCES "user"(id, displayname) ON DELETE SET NULL;\n"""

with open(mg_script_fname, "w") as mg_script:
    for table_name in tables:
        print(table_sql.format(table_name=table_name), file=mg_script)

