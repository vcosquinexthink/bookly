package com.bookly.inventory.domain.model

import com.bookly.book.domain.model.Book
import com.bookly.bookstore.domain.model.BookstoreId
import com.bookly.inventory.application.BookNotAvailableException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class InventoryItemTest {
    
    @Test
    fun `reserve decrements available when units are available`() {
        val item =
            InventoryItem(Book.BookId("isbn"), total = 5, available = 2, bookstoreId = BookstoreId(UUID.randomUUID()))
        val reserved = item.reserve()
        assertEquals(1, reserved.available)
        assertEquals(item.total, reserved.total)
        assertEquals(item.bookId, reserved.bookId)
        assertEquals(item.bookstoreId, reserved.bookstoreId)
    }

    @Test
    fun `reserve throws exception when no units are available`() {
        val item =
            InventoryItem(Book.BookId("isbn"), total = 5, available = 0, bookstoreId = BookstoreId(UUID.randomUUID()))
        assertThrows<BookNotAvailableException> {
            item.reserve()
        }
    }
}

