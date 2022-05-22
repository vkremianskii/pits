CREATE TABLE haul_cycle
(
    id bigserial NOT NULL PRIMARY KEY,
    truck_id int NOT NULL,
    shovel_id int,
    wait_load_timestamp timestamp 
    create_timestamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_timestamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX hau_cyc_tru_id_idx ON haul_cycle (truck_id);
