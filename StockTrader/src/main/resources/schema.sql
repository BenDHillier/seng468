-- TODO: properly config application.properties so I don't have to drop tables.
DROP TABLE IF EXISTS pending_buy;
DROP TABLE IF EXISTS pending_sell;
DROP TABLE IF EXISTS investment;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS account_transaction_log;
DROP TABLE IF EXISTS buy_trigger;
DROP TABLE IF EXISTS sell_trigger;
DROP TABLE IF EXISTS log_xml;
DROP TABLE IF EXISTS logs;
DROP TABLE IF EXISTS logging_event_exception;
DROP TABLE IF EXISTS logging_event_property;
DROP TABLE IF EXISTS logging_event;

CREATE TABLE pending_buy (
    id integer PRIMARY KEY,
    price integer,
    amount integer CHECK (amount >= 0),
    timestamp bigint,
    time_created bigint,
    user_id varchar(255),
    stock_symbol varchar(255)
);

CREATE TABLE pending_sell (
    id integer PRIMARY KEY,
    stock_count integer CHECK (stock_count >= 0),
    stock_price integer,
    timestamp bigint,
    time_created bigint,
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
--     funds float,
    funds NUMERIC(12,2),
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

CREATE TABLE logs
(
  event_id          SERIAL PRIMARY KEY,
  logtype character varying(255),
  command character varying(255),
  timestamp character varying(255),
  quote_server_time character varying(255),
  server character varying(255),
  transaction_num character varying(255),
  action character varying(255),
  username character varying(255),
  stock_symbol character varying(255),
  filename character varying(255),
  funds character varying(255),
  price character varying(255),
  cryptokey character varying(255),
  error_message character varying(255),
  debug_message character varying(255)
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
      INSERT INTO logs (event_id, logtype, command, timestamp, quote_server_time, server, transaction_num, action, username, stock_symbol, filename, funds, price, cryptokey, error_message, debug_message)
      VALUES ((select nextval(''logs_event_id_seq'')),''AccountTransactionType'', ''NULL'', (trunc(extract(epoch from now()) * 1000))::varchar(255), ''NULL'', NEW.last_server, (NEW.last_transaction_number)::varchar(255), action, NEW.user_id, ''NULL'', ''NULL'',  funds::varchar(255), ''NULL'', ''NULL'' ,''NULL'', ''NULL'');
       INSERT INTO account_transaction_log (action, funds, timestamp, username, server, "transactionNum")
       VALUES (action, funds, trunc(extract(epoch from now()) * 1000), NEW.user_id, NEW.last_server, NEW.last_transaction_number);
       WITH temp (action,funds,timestamp,username, server, "transactionNum") AS (values (action, TRUNC((SELECT CAST(funds AS NUMERIC(12,2))/100.00),2), trunc(extract(epoch from now()) * 1000), NEW.user_id, NEW.last_server, NEW.last_transaction_number))
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


CREATE OR REPLACE FUNCTION
increment_amount_after_set_cost(active_user varchar(255), ss varchar(255), amount_inc integer)
    RETURNS integer AS '
DECLARE
BEGIN
    WITH trigger AS
        (SELECT * FROM sell_trigger WHERE user_id = active_user AND stock_symbol = ss),
    stocks_to_remove AS (SELECT
        ((trigger.stock_amount % trigger.stock_cost) + amount_inc) / trigger.stock_cost
        FROM trigger)
    UPDATE investment SET stock_count = stock_count - (TABLE stocks_to_remove);
    UPDATE sell_trigger SET stock_amount = stock_amount + amount_inc
    WHERE user_id = active_user AND stock_symbol = ss;
    RETURN 1;
END;
' LANGUAGE plpgsql;



DROP SEQUENCE IF EXISTS hibernate_sequence;
CREATE SEQUENCE hibernate_sequence START 1;
