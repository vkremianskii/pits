-- Equipment

CREATE TABLE equipment
(
    id uuid NOT NULL PRIMARY KEY,
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
    id uuid NOT NULL PRIMARY KEY,
    name varchar(64) NOT NULL,
    type varchar(16) NOT NULL
);

-- Location points

CREATE TABLE location_point
(
    id serial NOT NULL PRIMARY KEY,
    location_id uuid NOT NULL,
    point_order smallint NOT NULL,
    latitude numeric(10, 8) NOT NULL,
    longitude numeric(11, 8) NOT NULL,
    CONSTRAINT loc_poi_loc_id_fk FOREIGN KEY (location_id) REFERENCES location (id)
);

CREATE INDEX loc_poc_loc_id_idx ON location_point (location_id)
