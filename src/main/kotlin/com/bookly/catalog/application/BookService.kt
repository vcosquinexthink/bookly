package com.bookly.catalog.application

import com.bookly.catalog.domain.model.Book
import com.bookly.catalog.domain.model.valueobject.BookId
import com.bookly.catalog.infrastructure.repository.BookRepository
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import org.springframework.stereotype.Service

@Service
class BookService(
    private val bookRepository: BookRepository,
) {

    fun addOrUpdateBookReference(book: Book): Book {
        return bookRepository.save(book)
    }

    fun getBookById(bookId: BookId): Book {
        return try {
            bookRepository.getReferenceById(bookId.isbn)
        } catch (ex: JpaObjectRetrievalFailureException) {
            throw BookNotFoundException("Book with id ${bookId.isbn} not found")
        }
    }
}

class BookNotFoundException(message: String) : RuntimeException(message)
