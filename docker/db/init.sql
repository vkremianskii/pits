CREATE DATABASE registry;
CREATE USER registry WITH PASSWORD 'registry';
GRANT ALL PRIVILEGES ON DATABASE registry to registry;

CREATE DATABASE processes;
CREATE USER processes WITH PASSWORD 'processes';
GRANT ALL PRIVILEGES ON DATABASE processes to processes;
