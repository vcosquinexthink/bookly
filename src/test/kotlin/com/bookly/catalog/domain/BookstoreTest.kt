package com.bookly.catalog.domain

import com.bookly.catalog.domain.model.Bookstore
import com.bookly.catalog.domain.model.valueobject.BookId
import com.bookly.catalog.domain.model.valueobject.BookstoreId
import com.bookly.catalog.domain.model.valueobject.BookstoreName
import com.bookly.catalog.domain.model.valueobject.Location
import org.junit.jupiter.api.Assertions.*
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

    @Test
    fun `should return all inventory items`() {
        val bookstore = Bookstore(
            BookstoreId(UUID.randomUUID()),
            BookstoreName("Test Store"),
            Location(10),
            mutableMapOf()
        )

        bookstore.addBook(BookId("123"), 5)
        bookstore.addBook(BookId("456"), 3)

        val inventory = bookstore.getInventory()

        assertEquals(2, inventory.size) {
            "Expected 2 inventory items, but got ${inventory.size}"
        }

        val firstItem = inventory.find { it.bookId.isbn == "123" }
        assertNotNull(firstItem) {
            "Expected inventory to contain book with ISBN '123', but it was not found"
        }
        assertEquals(5, firstItem?.total)
        assertEquals(5, firstItem?.available)

        val secondItem = inventory.find { it.bookId.isbn == "456" }
        assertNotNull(secondItem) {
            "Expected inventory to contain book with ISBN '456', but it was not found"
        }
        assertEquals(3, secondItem?.total)
        assertEquals(3, secondItem?.available)
    }
}
