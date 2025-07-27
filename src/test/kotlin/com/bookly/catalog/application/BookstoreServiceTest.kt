package com.bookly.catalog.application

import com.bookly.catalog.application.BookstoreService.BookstoreNotFoundException
import com.bookly.catalog.domain.model.valueobject.BookstoreName
import com.bookly.catalog.domain.model.valueobject.Location
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.util.*

class BookstoreServiceTest {
    @Test
    fun `should return bookstores ordered by proximity to given location`() {
        val service = BookstoreService(mutableListOf())
        val b1 = service.createBookstore(BookstoreName("A"), Location(10))
        val b2 = service.createBookstore(BookstoreName("B"), Location(5))
        val b3 = service.createBookstore(BookstoreName("C"), Location(7))
        val b4 = service.createBookstore(BookstoreName("D"), Location(20))

        val ordered = service.listBookstoresOrderedByProximity(7)
        val expected = listOf(b3, b2, b1, b4) // 7(0), 5(2), 10(-3), 20(-13)
        assertEquals(
            expected.map { it.name.value },
            ordered.map { it.name.value },
            "Expected order: ${expected.map { it.name.value }}, but got: ${ordered.map { it.name.value }}"
        )
    }

    @Test
    fun `should return bookstore by ID`() {
        val service = BookstoreService(mutableListOf())
        val bookstore = service.createBookstore(BookstoreName("Test Store"), Location(10))

        val retrievedBookstore = service.getBookstoreById(bookstore.id.value)

        assertNotNull(retrievedBookstore) {
            "Expected to retrieve a bookstore, but got null"
        }
        assertEquals(bookstore.id, retrievedBookstore.id) {
            "Expected bookstore ID to be ${bookstore.id}, but got ${retrievedBookstore.id}"
        }
        assertEquals(bookstore.name, retrievedBookstore.name) {
            "Expected bookstore name to be ${bookstore.name}, but got ${retrievedBookstore.name}"
        }
    }

    @Test
    fun `should throw exception when retrieving non-existent bookstore by ID`() {
        val service = BookstoreService(mutableListOf())
        val nonExistentBookstoreId = UUID.randomUUID()

        val exception = org.junit.jupiter.api.assertThrows<BookstoreNotFoundException> {
            service.getBookstoreById(nonExistentBookstoreId)
        }

        assertEquals("Bookstore with ID $nonExistentBookstoreId not found", exception.message) {
            "Expected exception message to be 'Bookstore with ID $nonExistentBookstoreId not found', but got: ${exception.message}"
        }
    }
}
