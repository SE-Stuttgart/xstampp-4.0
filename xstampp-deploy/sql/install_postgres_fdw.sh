#!/bin/bash -x
echo -e "# TYPE  DATABASE        USER            ADDRESS                 METHOD
# 'local' is for Unix domain socket connections only
local   all             all                                     trust
# IPv4 local connections: we need to leave the following line out so that user xstampp needs a password when connecting to remote db - important for postgres fdw
# host    all             all             127.0.0.1/32            trust
# IPv6 local connections:
host    all             all             ::1/128                 trust
# Allow replication connections from localhost, by a user with the
# replication privilege.
local   replication     all                                     trust
host    replication     all             127.0.0.1/32            trust
host    replication     all             ::1/128                 trust

host all all all md5" > "$PGDATA/pg_hba.conf"

psql -U postgres xstampp-master  -c "CREATE EXTENSION postgres_fdw;
CREATE SERVER project_server
        FOREIGN DATA WRAPPER postgres_fdw
        OPTIONS (host 'localhost', port '5432', dbname 'xstampp-project');"

psql -U postgres xstampp-master  -c "CREATE USER MAPPING FOR xstampp
        SERVER project_server
        OPTIONS (user 'xstampp', password '$PG_XSTAMPP_PASSWORD');"

psql -U postgres xstampp-master  -c "CREATE FOREIGN TABLE project_remote (
    id uuid NOT NULL,
    lock_holder_id uuid,
    lock_holder_displayname character varying(128) COLLATE pg_catalog.\"default\",
    lock_exp_time timestamp with time zone
)
        SERVER project_server
        OPTIONS (schema_name 'public', table_name 'project');"

psql -U postgres xstampp-master  -c "CREATE FOREIGN TABLE user_remote (
    id uuid NOT NULL,
    displayname character varying(128)
)
        SERVER project_server
        OPTIONS (schema_name 'public', table_name 'user');"

