-- Equipment positions

CREATE TABLE equipment_position
(
    id bigserial NOT NULL PRIMARY KEY,
    equipment_id uuid NOT NULL,
    latitude double precision NOT NULL,
    longitude double precision NOT NULL,
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

-- Haul cycles

CREATE TABLE haul_cycle
(
    id bigserial NOT NULL PRIMARY KEY,
    truck_id uuid NOT NULL,
    shovel_id uuid,
    wait_load_timestamp timestamp,
    start_load_timestamp timestamp,
    start_load_latitude double precision,
    start_load_longitude double precision,
    end_load_timestamp timestamp,
    end_load_payload integer,
    start_unload_timestamp timestamp,
    end_unload_timestamp timestamp,
    insert_timestamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX hau_cyc_tru_id_ins_tim_idx ON haul_cycle (truck_id, insert_timestamp DESC);
