-- Users

CREATE TABLE auth_user
(
    id uuid NOT NULL PRIMARY KEY,
    username varchar(32) NOT NULL,
    password varchar(32) NOT NULL,
    scopes text NOT NULL
);

CREATE INDEX aut_use_use_nam_idx ON auth_user (username);

INSERT INTO auth_user (id, username, password, scopes)
  VALUES ('c0d50fe1-7c01-44c6-a308-3c03b8677dca', 'admin', '3ySEyb7u9C+zx5CpIBYB0w==', '["admin"]');
