-- TODO: properly config application.properties so I don't have to drop tables.
DROP TABLE IF EXISTS pending_buy;
DROP TABLE IF EXISTS pending_sell;
DROP TABLE IF EXISTS investment;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS account_transaction_log;
DROP TABLE IF EXISTS user_command_log;
DROP TABLE IF EXISTS buy_trigger;

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

CREATE TABLE buy_trigger (
  --id SERIAL PRIMARY KEY,
  stock_amount integer CHECK (stock_amount >= 0),
  stock_cost integer,
  timestamp timestamp DEFAULT CURRENT_TIMESTAMP,
  user_id varchar(255),
  stock_symbol varchar(255),
  PRIMARY KEY (user_id, stock_symbol)
);

CREATE TABLE account_transaction_log (
    action varchar,
    funds integer,
    timestamp timestamp,
    username varchar
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