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

INSERT INTO equipment (name, type) VALUES ('Dozer No.1', 'dozer');
INSERT INTO equipment (name, type) VALUES ('Drill No.1', 'drill');
INSERT INTO equipment (name, type, load_radius) VALUES ('Shovel No.1', 'shovel', 20);
INSERT INTO equipment (name, type) VALUES ('Truck No.1', 'truck');

-- Locations

CREATE TABLE location
(
    id serial NOT NULL PRIMARY KEY,
    name varchar(64) NOT NULL,
    type varchar(16) NOT NULL
);

INSERT INTO location (name, type) VALUES ('Hole No.1', 'hole');
INSERT INTO location (name, type) VALUES ('Face No.1', 'face');
INSERT INTO location (name, type) VALUES ('Stockpile No.1', 'stockpile');
INSERT INTO location (name, type) VALUES ('Dump No.1', 'dump');
