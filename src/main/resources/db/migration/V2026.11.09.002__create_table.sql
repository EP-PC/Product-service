CREATE TABLE products.products (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(256) NOT NULL,
    price NUMERIC(12,2) NOT NULL,
    unit VARCHAR(32) NOT NULL
);
