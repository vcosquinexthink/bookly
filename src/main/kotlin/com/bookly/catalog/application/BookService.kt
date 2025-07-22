package com.bookly.catalog.application

import com.bookly.catalog.domain.model.Book
import com.bookly.catalog.domain.model.valueobject.BookId
import org.springframework.stereotype.Service

@Service
class BookService {

    private val books = mutableMapOf<BookId, Book>()

    fun addOrUpdateBookReference(book: Book) {
        books[book.id] = book
    }

    fun getBookById(bookId: BookId): Book? {
        return books[bookId]
    }
}
