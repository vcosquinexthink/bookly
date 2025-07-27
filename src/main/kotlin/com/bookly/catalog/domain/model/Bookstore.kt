package com.bookly.catalog.domain.model

import com.bookly.catalog.domain.model.valueobject.BookId
import com.bookly.catalog.domain.model.valueobject.BookstoreId
import com.bookly.catalog.domain.model.valueobject.BookstoreName
import com.bookly.catalog.domain.model.valueobject.Location

data class InventoryItem(val bookId: BookId, val total: Int, val available: Int, val bookstoreId: BookstoreId)

class Bookstore(
    val id: BookstoreId,
    val name: BookstoreName,
    val location: Location,
)
