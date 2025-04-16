package com.bookstore;

import com.bookstore.model.Book;
import com.bookstore.service.BookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;

@SpringBootApplication
public class BookstoreApplication {
	public static void main(String[] args) {
		SpringApplication.run(BookstoreApplication.class, args);
	}

	@org.springframework.context.annotation.Bean
	public CommandLineRunner demo(BookService bookService) {
		return args -> {
			Book book = new Book();
			book.setTitle("Berserk");
			book.setAuthor("Kentaro Miura");
			book.setIsbn("978-617-7984-30-5");
			book.setPrice(new BigDecimal("39.99"));
			book.setDescription("The series follows the story of Guts");
			book.setCoverImage("Berserk.jpg");

			bookService.save(book);

			System.out.println("Books in Base:");
			bookService.findAll().forEach(System.out::println);
		};
	}
}
