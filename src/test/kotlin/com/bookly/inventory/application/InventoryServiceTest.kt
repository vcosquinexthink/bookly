package com.bookly.inventory.application

import com.bookly.book.domain.model.Book
import com.bookly.bookstore.domain.model.BookstoreId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID.randomUUID

class InventoryServiceTest {

    @Test
    fun `should create a new inventory`() {
        val bookstoreId = BookstoreId(randomUUID())
        val bookId = Book.BookId("123")

        val service = InventoryService(mutableMapOf())

        val returnedInventory = service.addInventoryItem(bookstoreId, bookId, 1)
        assert(returnedInventory.bookId == bookId)
        assert(returnedInventory.bookstoreId == bookstoreId)
        assert(returnedInventory.total == 1)
        assert(returnedInventory.available == 1)

        val newInventory = service.getInventory(bookstoreId, bookId)
        assert(newInventory.bookId == bookId)
        assert(newInventory.bookstoreId == bookstoreId)
        assert(newInventory.total == 1)
        assert(newInventory.available == 1)
    }

    @Test
    fun `should update an existing inventory`() {
        val bookstoreId = BookstoreId(randomUUID())
        val bookId = Book.BookId("123")

        val service = InventoryService(mutableMapOf())

        service.addInventoryItem(bookstoreId, bookId, 1)
        service.addInventoryItem(bookstoreId, bookId, 100)

        val newInventory = service.getInventory(bookstoreId, bookId)
        assert(newInventory.bookId == bookId)
        assert(newInventory.bookstoreId == bookstoreId)
        assert(newInventory.total == 101)
        assert(newInventory.available == 101)
    }

    @Test
    fun `should retrieve inventories for bookstore`() {
        val bookstoreId = BookstoreId(randomUUID())
        val bookId1 = Book.BookId("123")
        val bookId2 = Book.BookId("456")

        val service = InventoryService(mutableMapOf())

        service.addInventoryItem(bookstoreId, bookId1, 3)
        service.addInventoryItem(bookstoreId, bookId2, 101)

        val inventories = service.getInventoriesForBookstore(bookstoreId)
        assert(inventories.size == 2) { "Expected 2 inventory items, got ${inventories.size}" }
        assert(inventories.any { it.bookId == bookId1 && it.total == 3 && it.available == 3 }) {
            "Inventory for bookId1 should have total 3, available 3"
        }
        assert(inventories.any { it.bookId == bookId2 && it.total == 101 && it.available == 101 }) {
            "Inventory for bookId2 should have total 101, available 101"
        }
    }

    @Test
    fun `book reservation should decrement available units by one`() {
        val bookstoreId = BookstoreId(randomUUID())
        val bookId1 = Book.BookId("123")
        val service = InventoryService(mutableMapOf())
        service.addInventoryItem(bookstoreId, bookId1, 3)
        val inventory = service.getInventoriesForBookstore(bookstoreId).first()
        assert(inventory.bookId.isbn == "123")
        assert(inventory.total == 3)
        assert(inventory.available == 3)

        service.reserveBook(bookstoreId, bookId1)

        val newInventory = service.getInventoriesForBookstore(bookstoreId).first()
        assert(newInventory.bookId.isbn == "123")
        assert(newInventory.total == 3)
        assert(newInventory.available == 2)
    }

    @Test
    fun `book reservation can't be performed when no available units`() {
        val bookstoreId = BookstoreId(randomUUID())
        val bookId = Book.BookId("123")
        val service = InventoryService(mutableMapOf())

        // Add inventory with 1 unit
        service.addInventoryItem(bookstoreId, bookId, 1)

        // Reserve the only available unit
        service.reserveBook(bookstoreId, bookId)

        // Verify inventory state: total=1, available=0
        val inventory = service.getInventory(bookstoreId, bookId)
        assert(inventory.total == 1)
        assert(inventory.available == 0)

        // Attempt to reserve another unit should throw exception
        val exception = assertThrows<BookNotAvailableException> {
            service.reserveBook(bookstoreId, bookId)
        }

        assert(exception.message == "Book not available for reservation")
    }

    @Test
    fun `book reservation can't be performed when no inventory`() {
        val bookstoreId = BookstoreId(randomUUID())
        val bookId = Book.BookId("123")
        val service = InventoryService(mutableMapOf())

        // Attempt to reserve should throw exception
        val exception = assertThrows<BookNotAvailableException> {
            service.reserveBook(bookstoreId, bookId)
        }

        assert(exception.message == "Book not available for reservation")
    }
}
