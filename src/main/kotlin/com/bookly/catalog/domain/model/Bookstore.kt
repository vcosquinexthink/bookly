package com.bookly.catalog.domain.model

import com.bookly.catalog.domain.model.valueobject.BookId
import com.bookly.catalog.domain.model.valueobject.Location
import java.util.UUID

class Bookstore(
    val id: UUID,
    val name: String,
    val location: Location,
    private val inventory: MutableMap<Book, Int> // Book to total count
) {
    fun addBook(book: Book, count: Int = 1) {
        inventory[book] = (inventory[book] ?: 0) + count
    }
    fun findBook(isbn: BookId): Book? = inventory.keys.find { it.id == isbn }
    fun getInventoryForBook(isbn: BookId): InventoryItem? {
        val book = findBook(isbn) ?: return null
        val total = inventory[book] ?: 0
        // For simplicity, assume all are available
        return InventoryItem(book, total, total)
    }
    fun listInventory(): List<InventoryItem> = inventory.map { (book, total) ->
        InventoryItem(book, total, total)
    }
}

data class InventoryItem(val book: Book, val total: Int, val available: Int)
