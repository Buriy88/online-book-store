package com.bookstore.repository;

import java.util.List;

public record BookSearchParametersDto(List<String> titles,
                                      List<String> authors,
                                      List<String> isbns) {

}
