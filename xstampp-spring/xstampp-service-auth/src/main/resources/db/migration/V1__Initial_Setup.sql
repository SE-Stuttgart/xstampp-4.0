
CREATE TABLE "user"
(
    id uuid NOT NULL,
    email_address character varying(128) NOT NULL,
    password character varying(256) NOT NULL,
    display_name character varying(128) NOT NULL,
    is_system_admin boolean NOT NULL,
    num_unsuccessful_attempts INTEGER,
    locked_until timestamp(3) with time zone,
    unlock_token uuid,

    PRIMARY KEY (id),
    UNIQUE (email_address)
);


CREATE TABLE "group"
(
    id uuid NOT NULL,
    name character varying(128),
    private boolean NOT NULL,
    description text,

    PRIMARY KEY (id)
);


-- master project table is weakly linked to the project manager (separate database)
-- see XSTAMPP 4.0 architecture concept for details and reasoning
CREATE TABLE "project"
(
    id uuid NOT NULL,
    name character varying(128) NOT NULL,
    description text,
    created_at timestamp(3) with time zone NOT NULL,
    group_id uuid REFERENCES "group" (id),
    reference_number character varying(128),

    PRIMARY KEY (id)
);


CREATE TYPE rights AS ENUM ('Group_Leader', 'Analyst', 'Developer', 'Guest');
CREATE TABLE "group_membership"
(
    user_id uuid REFERENCES "user" (id) ON DELETE CASCADE,
    group_id uuid REFERENCES "group" (id) ON DELETE CASCADE,
    access_level character varying (32) NOT NULL,

    PRIMARY KEY (user_id, group_id)
);
