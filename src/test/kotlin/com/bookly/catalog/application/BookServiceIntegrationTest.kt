package com.bookly.catalog.application

import com.bookly.catalog.domain.model.Book
import com.bookly.catalog.domain.model.valueobject.BookAuthor
import com.bookly.catalog.domain.model.valueobject.BookId
import com.bookly.catalog.domain.model.valueobject.BookTitle
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
        val bookId = BookId("B78B6F4F-ISBN")
        val book = Book(bookId, BookTitle("Test Book"), BookAuthor("Test Author"))
        bookService.addOrUpdateBookReference(book)

        val retrieved = bookService.getBookById(bookId)

        assertNotNull(retrieved)
        assertEquals("Test Book", retrieved!!.getTitle().value)
        assertEquals("Test Author", retrieved!!.getAuthor().value)
    }
}
