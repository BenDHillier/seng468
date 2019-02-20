-- TODO: properly config application.properties so I don't have to drop tables.
DROP TABLE IF EXISTS pending_buy;
DROP TABLE IF EXISTS pending_sell;
DROP TABLE IF EXISTS investment;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS account_transaction_log;
DROP TABLE IF EXISTS buy_trigger;
DROP TABLE IF EXISTS sell_trigger;
DROP TABLE IF EXISTS log_xml;
DROP TABLE IF EXISTS "userCommand";
DROP TABLE IF EXISTS logging_event_exception;
DROP TABLE IF EXISTS logging_event_property;
DROP TABLE IF EXISTS logging_event;

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

CREATE TABLE buy_trigger (
  --id SERIAL PRIMARY KEY,
  stock_amount integer CHECK (stock_amount >= 0),
  stock_cost integer,
  timestamp timestamp DEFAULT CURRENT_TIMESTAMP,
  user_id varchar(255),
  stock_symbol varchar(255),
  PRIMARY KEY (user_id, stock_symbol)
);

CREATE TABLE sell_trigger (
  --id SERIAL PRIMARY KEY,
  stock_amount integer CHECK (stock_amount >= 0),
  stock_cost integer,
  timestamp timestamp DEFAULT CURRENT_TIMESTAMP,
  user_id varchar(255),
  stock_symbol varchar(255),
  PRIMARY KEY (user_id, stock_symbol)
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

-- CREATE TABLE usercommand (
--   id SERIAL PRIMARY KEY,
--   EVENT_DATE varchar,
--   LEVEL varchar,
--   LOGGER  varchar,
--   MESSAGE  varchar,
--   THROWABLE  varchar
-- );


CREATE TABLE logging_event
(
  timestmp         BIGINT NOT NULL,
  formatted_message  TEXT NOT NULL,
  logger_name       VARCHAR(254) NOT NULL,
  level_string      VARCHAR(254) NOT NULL,
  thread_name       VARCHAR(254),
  reference_flag    SMALLINT,
  arg0              VARCHAR(254),
  arg1              VARCHAR(254),
  arg2              VARCHAR(254),
  arg3              VARCHAR(254),
  caller_filename   VARCHAR(254) NOT NULL,
  caller_class      VARCHAR(254) NOT NULL,
  caller_method     VARCHAR(254) NOT NULL,
  caller_line       CHAR(4) NOT NULL,
  event_id          SERIAL PRIMARY KEY
);

CREATE TABLE logging_event_property
(
  event_id	      BIGINT NOT NULL,
  mapped_key        VARCHAR(254) NOT NULL,
  mapped_value      VARCHAR(1024),
  PRIMARY KEY(event_id, mapped_key),
  FOREIGN KEY (event_id) REFERENCES logging_event(event_id)
);

CREATE TABLE logging_event_exception
(
  event_id         BIGINT NOT NULL,
  i                SMALLINT NOT NULL,
  trace_line       VARCHAR(254) NOT NULL,
  PRIMARY KEY(event_id, i),
  FOREIGN KEY (event_id) REFERENCES logging_event(event_id)
);

CREATE TABLE "userCommand"
(
  timestamp character varying(255),
  server character varying(255),
  "transactionNum" character varying(255),
  command character varying(255),
  username character varying(255),
  "stockSymbol" character varying(255),
  filename character varying(255),
  funds character varying(255),
  event_id          SERIAL PRIMARY KEY
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