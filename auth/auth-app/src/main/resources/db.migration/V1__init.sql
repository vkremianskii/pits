-- Users

CREATE TABLE user
(
    id uuid NOT NULL PRIMARY KEY,
    username varchar(32) NOT NULL,
    password varchar(24) NOT NULL,
    scopes jsonb NOT NULL
);

CREATE INDEX use_use_nam_idx ON user (username);
