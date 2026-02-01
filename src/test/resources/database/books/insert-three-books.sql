DELETE FROM books_categories;
DELETE FROM books;
DELETE FROM categories;

ALTER TABLE books ALTER COLUMN id RESTART WITH 1;
ALTER TABLE categories ALTER COLUMN id RESTART WITH 1;

INSERT INTO categories (name, description, is_deleted)
VALUES ('Fiction', 'Fictional books', false),
       ('Science', 'Scientific literature', false),
       ('Programming', 'Books about software development', false);

INSERT INTO books (title, author, isbn, price, description, cover_image, deleted)
VALUES ('The Hobbit', 'J.R.R. Tolkien', '978-0547928227', 15.99, 'Fantasy novel', NULL, false),
       ('A Brief History of Time', 'Stephen Hawking', '978-0553380163', 18.50, 'Cosmology and science', NULL, false),
       ('Clean Code', 'Robert C. Martin', '978-0132350884', 30.00, 'A handbook of agile software craftsmanship', NULL,
        false);


INSERT INTO books_categories (book_id, category_id)
VALUES (1, 1),
       (2, 2),
       (3, 3);

