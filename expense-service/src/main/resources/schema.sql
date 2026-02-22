-- USER TABLE
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE
);

-- ACCOUNT TABLE
CREATE TABLE account (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100)  NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_account_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- TRANSACTION TYPE : INCOME/EXPENSE
CREATE TYPE transaction_type AS ENUM('INCOME', 'EXPENSE');

-- CATEGORY TABLE
CREATE TABLE category (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_category_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- TRANSACTION TABLE
CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    amount DECIMAL(15, 2) NOT NULL,
    description TEXT,
    transaction_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    type transaction_type NOT NULL,
    account_id BIGINT NOT NULL,
    category_id BIGINT,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_account FOREIGN KEY (account_id) REFERENCES account(id),
    CONSTRAINT fk_transaction_category FOREIGN KEY (category_id) REFERENCES category(id),
    CONSTRAINT fk_transaction_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT ck_amount_positive CHECK (amount>0)
);

-- Indexes for performance
CREATE INDEX idx_transactions_user_date ON transaction(user_id, transaction_date DESC);
CREATE INDEX idx_transactions_account ON transaction(account_id);
CREATE INDEX idx_category_user ON category(user_id);
CREATE INDEX idx_account_user ON account(user_id);