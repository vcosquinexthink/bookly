package com.bookly.catalog.domain

import com.bookly.catalog.domain.model.Bookstore
import com.bookly.catalog.domain.model.valueobject.BookId
import com.bookly.catalog.domain.model.valueobject.BookstoreId
import com.bookly.catalog.domain.model.valueobject.BookstoreName
import com.bookly.catalog.domain.model.valueobject.Location
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.util.*

class BookstoreTest {

    @Test
    fun `should return inventory item for existing book`() {
        val bookstore = Bookstore(
            BookstoreId(UUID.randomUUID()),
            BookstoreName("Test Store"),
            Location(10),
            mutableMapOf()
        )

        bookstore.addBook(BookId("123"), 5)

        val inventoryItem = bookstore.getInventoryForBook(BookId("123"))

        assertEquals("123", inventoryItem?.bookId?.isbn)
        assertEquals(5, inventoryItem?.total)
        assertEquals(5, inventoryItem?.available)
        assertEquals(bookstore, inventoryItem?.bookstore)
    }

    @Test
    fun `should return null for non-existing book`() {
        val bookstore = Bookstore(
            BookstoreId(UUID.randomUUID()), BookstoreName("Test Store"), Location(10), mutableMapOf()
        )

        val inventoryItem = bookstore.getInventoryForBook(BookId("123"))

        assertNull(inventoryItem)
    }
}
