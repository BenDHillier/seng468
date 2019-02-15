-- TODO: properly config application.properties so I don't have to drop tables.
DROP TABLE IF EXISTS pending_buy;
DROP TABLE IF EXISTS pending_sell;
DROP TABLE IF EXISTS investment;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS account_transaction_log;
DROP TABLE IF EXISTS log_xml;

CREATE TABLE pending_buy (
    id integer PRIMARY KEY,
    price integer,
    amount integer CHECK (amount >= 0),
    timestamp bigint,
    user_id varchar(255),
    stock_symbol varchar(255)
);

CREATE TABLE pending_sell (
    id integer PRIMARY KEY,
    stock_count integer CHECK (stock_count >= 0),
    stock_price integer,
    timestamp bigint,
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
    amount integer CHECK (amount >= 0),
    last_transaction_number integer,
    last_server varchar(255)
);

CREATE TABLE account_transaction_log (
    id SERIAL PRIMARY KEY,
    action varchar(255),
    funds integer,
    timestamp bigint,
    username varchar(255),
    server varchar(255),
    "transactionNum" integer
);

CREATE TABLE log_xml (
    id SERIAL PRIMARY KEY,
    xml_log_entry varchar,
    user_id varchar(255)
);


-- FIX ME need to make accountTransaction log transaction_num flow with the parent command
-- (ie buy, add, sell, commit_sell, etc)
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
--       INSERT INTO account_transaction_log (action, funds, timestamp, username)
      INSERT INTO account_transaction_log (action, funds, timestamp, username, server, "transactionNum")
      VALUES (action, funds, trunc(extract(epoch from now()) * 1000), NEW.user_id, NEW.last_server, NEW.last_transaction_number);
      WITH temp (action,funds,timestamp,username, server, "transactionNum") AS (values (action, funds, trunc(extract(epoch from now()) * 1000), NEW.user_id, NEW.last_server, NEW.last_transaction_number))
      INSERT INTO log_xml (id, xml_log_entry,user_id)
      VALUES(
        (select nextval(''hibernate_sequence'')),
        (select xmlelement(name "accountTransaction", xmlforest(temp.action,temp.funds,temp.timestamp,temp.username, temp.server, temp."transactionNum")) from temp),
        (select temp.username from temp));
      RETURN NULL;
END;
' LANGUAGE plpgsql;

CREATE TRIGGER log_account_transaction AFTER INSERT OR UPDATE ON account
    FOR EACH ROW EXECUTE PROCEDURE log_account_transaction();

DROP SEQUENCE IF EXISTS hibernate_sequence;
CREATE SEQUENCE hibernate_sequence START 1;