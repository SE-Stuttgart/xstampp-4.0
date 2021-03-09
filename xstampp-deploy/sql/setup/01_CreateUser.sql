-- Create user for the application with limited rights.

CREATE USER postgresuser WITH
  LOGIN
  NOSUPERUSER
  INHERIT
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION;
  
REVOKE ALL PRIVILEGES ON ALL TABLES IN SCHEMA public FROM postgresuser;

GRANT SELECT,INSERT,UPDATE,DELETE ON ALL TABLES IN SCHEMA public TO postgresuser;