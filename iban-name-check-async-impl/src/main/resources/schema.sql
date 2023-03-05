CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS iban_name_entity (
   correlation_id uuid DEFAULT uuid_generate_v4 (),
   counter_party_name VARCHAR(255) NOT NULL,
   counter_party_account VARCHAR(255) NOT NULL,
   PRIMARY KEY (counter_party_account)
);