DROP TABLE IF EXISTS iban_name_entity;
DROP TABLE IF EXISTS iban_name_response_entity;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS iban_name_entity (
    transaction_id UUID DEFAULT uuid_generate_v4 (),
    correlation_id UUID DEFAULT uuid_generate_v4 (),
    counter_party_name VARCHAR(255) NOT NULL,
    counter_party_account VARCHAR(255) NOT NULL,
    PRIMARY KEY (transaction_id)
);

CREATE TABLE IF NOT EXISTS iban_name_response_entity (
    correlation_id uuid DEFAULT uuid_generate_v4 (),
    counter_party_account VARCHAR(255) NOT NULL,
    counter_party_name VARCHAR(255) NOT NULL,
    transaction_id UUID DEFAULT uuid_generate_v4 (),
    matching_result VARCHAR(255) NOT NULL,
    account_status VARCHAR(255) NOT NULL,
    account_holder_type VARCHAR(255) NOT NULL,
    switching_service_status VARCHAR(255) NOT NULL,
    switched_to_iban VARCHAR(255) NOT NULL,
    message VARCHAR(255) NOT NULL,
    PRIMARY KEY (counter_party_account)
);