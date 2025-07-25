package com.bookly.catalog.application

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

    fun addBook(bookstoreId: UUID, bookId: BookId, count: Int = 1) =
        (bookstores.find { it.id.value == bookstoreId }
            ?: throw BookstoreNotFoundException("Bookstore with ID $bookstoreId not found"))
            .addBook(bookId, count)

    fun listBookstoresOrderedByProximity(location: Int): List<Bookstore> =
        bookstores.sortedBy { abs(it.location.value - location) }

    fun getBookstoreById(bookstoreId: UUID): Bookstore = bookstores.find { it.id.value == bookstoreId } ?:
        throw BookstoreNotFoundException("Bookstore with ID $bookstoreId not found")

    fun clearBookstores() {
        bookstores.clear()
    }

    class BookstoreNotFoundException(message: String) : RuntimeException(message)
}
