package com.bookly.catalog.application

import com.bookly.catalog.domain.model.Book
import com.bookly.catalog.domain.model.Bookstore
import com.bookly.catalog.domain.model.valueobject.BookId
import com.bookly.catalog.domain.model.valueobject.BookstoreId
import com.bookly.catalog.domain.model.valueobject.BookstoreName
import com.bookly.catalog.domain.model.valueobject.Location
import org.springframework.stereotype.Service
import java.util.*
import kotlin.math.abs

@Service
class BookstoreService(private val bookstores: MutableList<Bookstore> = mutableListOf<Bookstore>()) {

    fun createBookstore(name: BookstoreName, location: Location): Bookstore {
        val bookstore = Bookstore(BookstoreId(UUID.randomUUID()), name, location, mutableMapOf())
        bookstores.add(bookstore)
        return bookstore
    }

    fun addBook(bookstoreId: UUID, bookId: BookId, count: Int = 1): Boolean {
        // todo: check if the bookstore exists, throw exception if not
        val bookstore = bookstores.find { it.id.value == bookstoreId } ?: return false
        bookstore.addBook(bookId, count)
        return true
    }

    fun listBookstores(): List<Bookstore> = bookstores

    fun listBookstoresOrderedByProximity(location: Int): List<Bookstore> {
        return listBookstores().sortedBy { abs(it.location.value - location) }
    }
}
