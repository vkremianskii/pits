CREATE TABLE haul_cycle
(
    id bigserial NOT NULL PRIMARY KEY,
    truck_id uuid NOT NULL,
    shovel_id uuid,
    wait_load_timestamp timestamp,
    start_load_timestamp timestamp,
    start_load_latitude numeric(10, 8),
    start_load_longitude numeric(11, 8),
    end_load_timestamp timestamp,
    end_load_payload smallint,
    start_unload_timestamp timestamp,
    end_unload_timestamp timestamp,
    insert_timestamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX hau_cyc_tru_id_ins_tim_idx ON haul_cycle (truck_id, insert_timestamp DESC);
