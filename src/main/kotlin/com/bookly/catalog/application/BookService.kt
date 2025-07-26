package com.bookly.catalog.application

import com.bookly.catalog.domain.model.Book
import com.bookly.catalog.domain.model.valueobject.BookId
import com.bookly.catalog.infrastructure.repository.BookRepository
import org.springframework.stereotype.Service

@Service
class BookService(
    private val bookRepository: BookRepository,
) {

    fun addOrUpdateBookReference(book: Book) {
        bookRepository.save(book)
    }

    fun getBookById(bookId: BookId): Book? {
        return bookRepository.getReferenceById(bookId.isbn)
    }
}
