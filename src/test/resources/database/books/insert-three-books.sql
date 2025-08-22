

CREATE TABLE IF NOT EXISTS categories (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          name VARCHAR(255) NOT NULL,
                                          description VARCHAR(1000),
                                          is_deleted BOOLEAN DEFAULT FALSE
);
CREATE TABLE IF NOT EXISTS books (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     title VARCHAR(255) NOT NULL,
                                     author VARCHAR(255) NOT NULL,
                                     isbn VARCHAR(50),
                                     price DECIMAL(10,2),
                                     description VARCHAR(2000),
                                     cover_image VARCHAR(500),
                                     deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS books_categories (
                                                book_id BIGINT NOT NULL,
                                                category_id BIGINT NOT NULL,
                                                PRIMARY KEY (book_id, category_id),
                                                CONSTRAINT fk_book FOREIGN KEY (book_id) REFERENCES books(id),
                                                CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

INSERT INTO categories (name, description, is_deleted)
VALUES
    ('Fiction', 'Fictional books', false),
    ('Science', 'Scientific literature', false),
    ('Programming', 'Books about software development', false);

INSERT INTO books (title, author, isbn, price, description, cover_image, deleted)
VALUES
    ('The Hobbit', 'J.R.R. Tolkien', '978-0547928227', 15.99, 'Fantasy novel', NULL, false),
    ('A Brief History of Time', 'Stephen Hawking', '978-0553380163', 18.50, 'Cosmology and science', NULL, false),
    ('Clean Code', 'Robert C. Martin', '978-0132350884', 30.00, 'A handbook of agile software craftsmanship', NULL, false);


INSERT INTO books_categories (book_id, category_id) VALUES (1, 1);


INSERT INTO books_categories (book_id, category_id) VALUES (2, 2);


INSERT INTO books_categories (book_id, category_id) VALUES (3, 3);
