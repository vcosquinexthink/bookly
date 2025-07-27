package com.bookly.book.application

import com.bookly.book.domain.model.Book
import com.bookly.book.infrastructure.repository.BookRepository
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import org.springframework.stereotype.Service

@Service
class BookService(
    private val bookRepository: BookRepository,
) {

    fun addOrUpdateBookReference(book: Book): Book {
        return bookRepository.save(book)
    }

    fun getBookById(bookId: Book.BookId): Book {
        return try {
            bookRepository.getReferenceById(bookId.isbn)
        } catch (ex: JpaObjectRetrievalFailureException) {
            throw BookNotFoundException("Book with id ${bookId.isbn} not found")
        }
    }
}

class BookNotFoundException(message: String) : RuntimeException(message)
