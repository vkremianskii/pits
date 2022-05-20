-- Equipment

CREATE TYPE equipment_type AS ENUM ('DOZER', 'DRILL', 'SHOVEL', 'TRUCK');

CREATE TABLE equipment
(
    id serial NOT NULL PRIMARY KEY,
    name varchar(64) NOT NULL,
    type equipment_type NOT NULL,
    latitude numeric(10, 8),
    longitude numeric(11, 8),
    elevation smallint
);

INSERT INTO equipment (name, type) VALUES ('Dozer No.1', 'DOZER');
INSERT INTO equipment (name, type) VALUES ('Drill No.1', 'DRILL');
INSERT INTO equipment (name, type) VALUES ('Shovel No.1', 'SHOVEL');
INSERT INTO equipment (name, type) VALUES ('Truck No.1', 'TRUCK');

-- Locations

CREATE TYPE location_type AS ENUM ('HOLE', 'FACE', 'STOCKPILE', 'DUMP');

CREATE TABLE location
(
    id serial NOT NULL PRIMARY KEY,
    name varchar(64) NOT NULL,
    type location_type NOT NULL
);

INSERT INTO location (name, type) VALUES ('Hole No.1', 'HOLE');
INSERT INTO location (name, type) VALUES ('Face No.1', 'FACE');
INSERT INTO location (name, type) VALUES ('Stockpile No.1', 'STOCKPILE');
INSERT INTO location (name, type) VALUES ('Dump No.1', 'DUMP');
