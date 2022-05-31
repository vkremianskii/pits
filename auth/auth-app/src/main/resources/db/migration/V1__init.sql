-- Users

CREATE TABLE auth_user
(
    id uuid NOT NULL PRIMARY KEY,
    username varchar(32) NOT NULL,
    password varchar(32) NOT NULL,
    scopes text NOT NULL
);

CREATE INDEX aut_use_use_nam_idx ON auth_user (username);
