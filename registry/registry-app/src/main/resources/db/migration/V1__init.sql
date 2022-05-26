-- Equipment

CREATE TABLE equipment
(
    id serial NOT NULL PRIMARY KEY,
    name varchar(64) NOT NULL,
    type varchar(16) NOT NULL,
    state varchar(32),
    latitude numeric(10, 8),
    longitude numeric(11, 8),
    elevation smallint,
    payload integer,
    load_radius smallint
);

-- Locations

CREATE TABLE location
(
    id serial NOT NULL PRIMARY KEY,
    name varchar(64) NOT NULL,
    type varchar(16) NOT NULL
);
