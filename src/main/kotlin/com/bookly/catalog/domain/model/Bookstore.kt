package com.bookly.catalog.domain.model

import com.bookly.catalog.domain.model.valueobject.BookId
import com.bookly.catalog.domain.model.valueobject.BookstoreId
import com.bookly.catalog.domain.model.valueobject.BookstoreName
import com.bookly.catalog.domain.model.valueobject.Location

class Bookstore(
    val id: BookstoreId,
    val name: BookstoreName,
    val location: Location,
    private val inventory: MutableMap<BookId, Int>
) {
    fun addBook(bookId: BookId, count: Int = 1) {
        inventory[bookId] = (inventory[bookId] ?: 0) + count
    }

    fun getInventoryForBook(isbn: BookId): InventoryItem? {
        val total = inventory[isbn] ?: return null
        // For simplicity, assume all are available
        return InventoryItem(isbn, total, total, this)
    }
}

data class InventoryItem(val bookId: BookId, val total: Int, val available: Int, val bookstore: Bookstore)
