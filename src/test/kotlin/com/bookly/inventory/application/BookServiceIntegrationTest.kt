package com.bookly.inventory.application

import com.bookly.book.application.BookNotFoundException
import com.bookly.book.application.BookService
import com.bookly.book.domain.model.Book
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class BookServiceIntegrationTest {
    @Autowired
    private lateinit var bookService: BookService

    @Test
    fun `add and retrieve book`() {
        val bookId = Book.BookId("B78B6F4F-ISBN")
        val book = Book(bookId, Book.BookTitle("Test Book"), Book.BookAuthor("Test Author"))
        bookService.addOrUpdateBookReference(book)

        val retrieved = bookService.getBookById(bookId)

        assertNotNull(retrieved)
        assertEquals("Test Book", retrieved.getTitle().value)
        assertEquals("Test Author", retrieved.getAuthor().value)
    }

    @Test
    fun `non existing book`() {
        val exception = org.junit.jupiter.api.assertThrows<BookNotFoundException> {
            bookService.getBookById(Book.BookId("B78B6F4F-ISBN"))
        }
        assertEquals("Book with id B78B6F4F-ISBN not found", exception.message)
    }
}
