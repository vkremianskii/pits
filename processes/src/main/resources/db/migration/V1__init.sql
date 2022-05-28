-- Equipment positions

CREATE TABLE equipment_position
(
    id bigserial NOT NULL PRIMARY KEY,
    equipment_id uuid NOT NULL,
    latitude numeric(10, 8) NOT NULL,
    longitude numeric(11, 8) NOT NULL,
    elevation integer NOT NULL,
    insert_timestamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX equ_pos_equ_id_ins_tim_idx ON equipment_position (equipment_id, insert_timestamp DESC);

-- Equipment payloads

CREATE TABLE equipment_payload
(
    id bigserial NOT NULL PRIMARY KEY,
    equipment_id uuid NOT NULL,
    payload integer NOT NULL,
    insert_timestamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX equ_pay_equ_id_ins_tim_idx ON equipment_payload (equipment_id, insert_timestamp DESC);
