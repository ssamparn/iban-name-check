DROP TABLE IF EXISTS iban_name_entity;
DROP TABLE IF EXISTS iban_name_check_response_entity;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS iban_name_entity (
    correlation_id uuid DEFAULT uuid_generate_v4 (),
    counter_party_name VARCHAR(255) NOT NULL,
    counter_party_account VARCHAR(255) NOT NULL,
    PRIMARY KEY (counter_party_account)
);

CREATE TABLE IF NOT EXISTS iban_name_check_response_entity (
    correlation_id uuid DEFAULT uuid_generate_v4 (),
    counter_party_name VARCHAR(255) NOT NULL,
    counter_party_account VARCHAR(255) NOT NULL,
    final_result VARCHAR(255) NOT NULL,
    info VARCHAR(255) NOT NULL,
    suggested_name VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    account_holder_type VARCHAR(255) NOT NULL,
    PRIMARY KEY (counter_party_account)
);