-- Equipment positions

CREATE TABLE equipment_position
(
    id bigserial NOT NULL PRIMARY KEY,
    equipment_id int NOT NULL,
    latitude numeric(10, 8) NOT NULL,
    longitude numeric(11, 8) NOT NULL,
    elevation integer NOT NULL,
    insert_timestamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX equ_pos_equ_id_ins_tim_idx ON equipment_position (equipment_id, insert_timestamp DESC);

-- Truck payload weights

CREATE TABLE truck_payload_weight
(
    id bigserial NOT NULL PRIMARY KEY,
    equipment_id int NOT NULL,
    weight integer NOT NULL,
    insert_timestamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX tru_pay_equ_id_ins_tim_idx ON truck_payload_weight (equipment_id, insert_timestamp DESC);
