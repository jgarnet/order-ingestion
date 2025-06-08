CREATE TABLE products (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    price DECIMAL NOT NULL
);

CREATE TABLE orders (
    id BINARY(16) PRIMARY KEY,
    order_date TIMESTAMP
);

CREATE TABLE order_customers (
    order_id BINARY(16) PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(10)
);
ALTER TABLE order_customers ADD FOREIGN KEY (order_id) REFERENCES orders(id);

CREATE TABLE order_lines (
    order_id BINARY(16) NOT NULL,
    line_number INT,
    product_id VARCHAR(255) NOT NULL,
    quantity INT
);
ALTER TABLE order_lines ADD PRIMARY KEY (order_id, line_number);
ALTER TABLE order_lines ADD FOREIGN KEY (order_id) REFERENCES orders(id);
ALTER TABLE order_lines ADD FOREIGN KEY (product_id) REFERENCES products(id);