package com.bookly.catalog.application

import com.bookly.catalog.domain.model.Book
import com.bookly.catalog.domain.model.Bookstore
import com.bookly.catalog.domain.model.valueobject.BookId
import com.bookly.catalog.domain.model.valueobject.BookstoreId
import com.bookly.catalog.domain.model.valueobject.BookstoreName
import com.bookly.catalog.domain.model.valueobject.Location
import org.springframework.stereotype.Service
import java.util.*

@Service
class BookstoreService {
    private val bookstores = mutableListOf<Bookstore>()

    fun createBookstore(name: BookstoreName, location: Location): Bookstore {
        val bookstore = Bookstore(BookstoreId(UUID.randomUUID()), name, location, mutableMapOf())
        bookstores.add(bookstore)
        return bookstore
    }

    fun addBook(bookstoreId: UUID, book: Book, count: Int = 1): Boolean {
        val bookstore = bookstores.find { it.id.value == bookstoreId } ?: return false
        bookstore.addBook(book, count)
        return true
    }

    fun getInventoryForBook(bookstoreId: UUID, isbn: BookId): com.bookly.catalog.domain.model.InventoryItem? {
        val bookstore = bookstores.find { it.id.value == bookstoreId } ?: return null
        return bookstore.getInventoryForBook(isbn)
    }

    fun listInventory(bookstoreId: UUID): List<com.bookly.catalog.domain.model.InventoryItem> {
        val bookstore = bookstores.find { it.id.value == bookstoreId } ?: return emptyList()
        return bookstore.listInventory()
    }

    fun listBookstores(): List<Bookstore> = bookstores
}
