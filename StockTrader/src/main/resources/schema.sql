-- TODO: properly config application.properties so I don't have to drop tables.
DROP TABLE IF EXISTS pending_buy;
DROP TABLE IF EXISTS pending_sell;
DROP TABLE IF EXISTS investment;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS account_transaction_log;
DROP TABLE IF EXISTS user_command_log;
DROP TABLE IF EXISTS quote_server_log;
DROP TABLE IF EXISTS error_event_log;

CREATE TABLE pending_buy (
    id integer PRIMARY KEY,
    price integer,
    amount integer CHECK (amount >= 0),
    timestamp timestamp without time zone,
    user_id varchar(255),
    stock_symbol varchar(255)
);

CREATE TABLE pending_sell (
    id integer PRIMARY KEY,
    stock_count integer CHECK (stock_count >= 0),
    stock_price integer,
    timestamp timestamp without time zone,
    user_id varchar(255),
    stock_symbol varchar(255)
);

CREATE TABLE investment (
    owner varchar(255),
    stock_count integer CHECK (stock_count >= 0),
    stock_symbol varchar(255),
    PRIMARY KEY (owner, stock_symbol)
);

CREATE TABLE account (
    user_id varchar(255) PRIMARY KEY,
    amount integer CHECK (amount >= 0)
);

CREATE TABLE account_transaction_log (
    transaction_num SERIAL PRIMARY KEY,
    action varchar(255),
    funds integer,
    timestamp timestamp,
    username varchar(255)
);

CREATE TABLE user_command_log (
    transaction_num integer,
    timestamp timestamp without time zone,
    server varchar(255),
    command varchar(255),
    username varchar(255),
    stock_symbol varchar(255),
    filename varchar(255),
    funds integer
);

CREATE TABLE quote_server_log (
    timestamp BIGINT,
    server varchar(255),
    transaction_num SERIAL PRIMARY KEY,
    price integer,
    stock_symbol varchar(255),
    user_name varchar(255),
    quote_server_time BIGINT,
    cryptokey varchar(255)
);

CREATE TABLE error_event_log (
    timestamp BIGINT,
    server varchar(255),
    transaction_num SERIAL PRIMARY KEY,
    command varchar(255),
    user_name varchar(255),
    stock_symbol varchar(255),
    funds integer,
    error_message varchar(255)
);



CREATE OR REPLACE FUNCTION log_account_transaction() RETURNS trigger AS '
DECLARE
    action varchar;
    old_amount integer;
    funds integer;
BEGIN
    -- For inserts (ie. new accounts), treat previous amount as 0.
     IF (TG_OP = ''INSERT'') THEN
        old_amount = 0;
     ELSE
        old_amount = OLD.amount;
     END IF;
     IF (old_amount < NEW.amount) THEN
        action = ''add'';
        funds = New.amount - old_amount;
      ELSIF (old_amount > NEW.amount) THEN
        action = ''remove'';
        funds = old_amount - NEW.amount;
      ELSE
        RETURN NULL;
      END IF;
      INSERT INTO account_transaction_log (action, funds, timestamp, username)
      VALUES (action, funds, NOW(), NEW.user_id);
      RETURN NULL;
END;
' LANGUAGE plpgsql;

CREATE TRIGGER log_account_transaction AFTER INSERT OR UPDATE ON account
    FOR EACH ROW EXECUTE PROCEDURE log_account_transaction();

DROP SEQUENCE IF EXISTS hibernate_sequence;
CREATE SEQUENCE hibernate_sequence START 1;