CREATE TYPE CURRENCY_CODE AS ENUM ('USD', 'EUR', 'RUB');
CREATE TABLE users (
  id            BIGSERIAL                NOT NULL PRIMARY KEY,
  telegram_id   BIGINT                   NOT NULL UNIQUE,
  first_name    TEXT                     NOT NULL,
  last_name     TEXT,
  username      TEXT,
  language_code VARCHAR(10)              NOT NULL,
  create_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
  last_seen_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE TABLE accounts (
  id        BIGSERIAL                NOT NULL PRIMARY KEY,
  user_id   BIGINT                   NOT NULL REFERENCES users (id),
  title     VARCHAR(128)             NOT NULL,
  create_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE TABLE account_balances (
  account_id    BIGINT          NOT NULL REFERENCES accounts (id),
  currency_code CURRENCY_CODE   NOT NULL,
  balance       NUMERIC(128, 0) NOT NULL DEFAULT 0,
  PRIMARY KEY (account_id, currency_code)
);

CREATE INDEX accounts_user_id_idx
  ON accounts (user_id);
CREATE INDEX account_balances_account_id
  ON account_balances (account_id);

CREATE TYPE TRANSACTION_TYPE AS ENUM ('adjustment', 'transfer', 'deposit', 'payment');

CREATE TABLE transactions (
  id               BIGSERIAL                NOT NULL PRIMARY KEY,
  transaction_type TRANSACTION_TYPE         NOT NULL,
  from_account_id  BIGINT REFERENCES accounts (id),
  to_account_id    BIGINT REFERENCES accounts (id),
  currency_code    CURRENCY_CODE            NOT NULL,
  amount           NUMERIC(128, 0)          NOT NULL,
  create_at        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);
