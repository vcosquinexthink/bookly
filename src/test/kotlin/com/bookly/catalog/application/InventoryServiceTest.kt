package com.bookly.catalog.application

import com.bookly.book.domain.model.Book
import com.bookly.catalog.domain.model.BookstoreId
import org.junit.jupiter.api.Test
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
        assert(inventories.any { it.bookId == bookId1 && it.total == 3 }) { "Inventory for bookId1 should have total 3" }
        assert(inventories.any { it.bookId == bookId2 && it.total == 101 }) { "Inventory for bookId2 should have total 101" }
    }
}
