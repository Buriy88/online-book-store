package com.bookstore;

import java.math.BigDecimal;

public final class TestConstants {
    public static final String BOOK_TITLE = "Title";
    public static final String BOOK_AUTHOR = "Test Author";
    public static final String BOOK_ISBN = "1234567890";
    public static final String BOOK_COVER_IMAGE = "cover.jpg";
    public static final BigDecimal BOOK_PRICE = BigDecimal.valueOf(9.99);
    public static final String BOOK_DESCRIPTION = "Test Description";

    public static final Long BOOK_VALID_ID = 1L;
    public static final Long BOOK_INVALID_ID = 100L;

    public static final Long CATEGORY_ID = 1L;
    public static final Long CATEGORY_INVALID_ID = 1000L;
    public static final String CATEGORY_NAME = "Test Category";
    public static final String CATEGORY_DESCRIPTION = "Test Description";

    public static final String FICTION_CATEGORY = "Fiction";

    public static final Long HOBBIT_BOOK_ID = 1L;
    public static final String HOBBIT_BOOK_TITLE = "The Hobbit";
    public static final String HOBBIT_BOOK_AUTHOR = "J.R.R. Tolkien";
    public static final String HOBBIT_BOOK_ISBN = "978-0547928227";
    public static final BigDecimal HOBBIT_BOOK_PRICE = BigDecimal.valueOf(15.99);
    public static final String HOBBIT_BOOK_DESCRIPTION = "Fantasy novel";

    public static final BigDecimal BOOK_INVALID_PRICE = BigDecimal.valueOf(-10.99);
    public static final String BOOK_INVALID_ISBN = "978";
    public static final String BOOK_INVALID_TITLE = "J.R.R. Tolkien";
    public static final String BOOK_INVALID_AUTHOR = "Taras Shevchenko";

    private TestConstants() {
    }
}
