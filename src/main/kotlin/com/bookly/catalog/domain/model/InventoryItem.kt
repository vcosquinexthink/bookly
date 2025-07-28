package com.bookly.catalog.domain.model

import com.bookly.book.domain.model.Book

data class InventoryItem(val bookId: Book.BookId, val total: Int, val available: Int, val bookstoreId: BookstoreId)