CREATE TABLE IF NOT EXISTS categories
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    name
    VARCHAR
(
    255
) NOT NULL,
    description VARCHAR
(
    1000
),
    is_deleted BOOLEAN DEFAULT FALSE
    );
INSERT INTO categories (name, description, is_deleted)
VALUES ('Fiction', 'Fictional books', false),
       ('Science', 'Scientific literature', false),
       ('Programming', 'Books about software development', false);
