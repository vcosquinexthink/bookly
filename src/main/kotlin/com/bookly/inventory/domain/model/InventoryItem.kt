package com.bookly.inventory.domain.model

import com.bookly.book.domain.model.Book
import com.bookly.bookstore.domain.model.BookstoreId

data class InventoryItem(val bookId: Book.BookId, val total: Int, val available: Int, val bookstoreId: BookstoreId)