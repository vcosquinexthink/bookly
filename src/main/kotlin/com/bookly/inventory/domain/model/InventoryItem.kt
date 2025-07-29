package com.bookly.inventory.domain.model

import com.bookly.book.domain.model.Book
import com.bookly.bookstore.domain.model.BookstoreId
import com.bookly.inventory.application.BookNotAvailableException

data class InventoryItem(val bookId: Book.BookId, val total: Int, val available: Int, val bookstoreId: BookstoreId) {
    fun reserve(): InventoryItem {
        if (available <= 0) {
            throw BookNotAvailableException("Book not available for reservation")
        }
        return copy(available = available - 1)
    }
}
