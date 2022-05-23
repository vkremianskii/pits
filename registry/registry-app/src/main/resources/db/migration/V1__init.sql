-- Equipment

CREATE TYPE equipment_type AS ENUM ('dozer', 'drill', 'shovel', 'truck');

CREATE TYPE equipment_state AS ENUM (
    'truck_empty',
    'truck_wait_load',
    'truck_load',
    'truck_haul',
    'truck_unload');

CREATE TABLE equipment
(
    id serial NOT NULL PRIMARY KEY,
    name varchar(64) NOT NULL,
    type equipment_type NOT NULL,
    state equipment_state,
    latitude numeric(10, 8),
    longitude numeric(11, 8),
    elevation smallint,
    payload integer,
    load_radius smallint
);

INSERT INTO equipment (name, type) VALUES ('Dozer No.1', 'dozer');
INSERT INTO equipment (name, type) VALUES ('Drill No.1', 'drill');
INSERT INTO equipment (name, type, load_radius) VALUES ('Shovel No.1', 'shovel', 20);
INSERT INTO equipment (name, type) VALUES ('Truck No.1', 'truck');

-- Locations

CREATE TYPE location_type AS ENUM ('hole', 'face', 'stockpile', 'dump');

CREATE TABLE location
(
    id serial NOT NULL PRIMARY KEY,
    name varchar(64) NOT NULL,
    type location_type NOT NULL
);

INSERT INTO location (name, type) VALUES ('Hole No.1', 'hole');
INSERT INTO location (name, type) VALUES ('Face No.1', 'face');
INSERT INTO location (name, type) VALUES ('Stockpile No.1', 'stockpile');
INSERT INTO location (name, type) VALUES ('Dump No.1', 'dump');
