CREATE TABLE "user"
(
	id uuid NOT NULL,
	displayname character varying(128),
	Primary KEY(id),
	unique(id, displayname)
);