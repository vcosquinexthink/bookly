package com.bookly.bookstore.application

import com.bookly.bookstore.domain.model.Bookstore
import com.bookly.bookstore.domain.model.BookstoreId
import com.bookly.bookstore.domain.model.BookstoreName
import com.bookly.bookstore.domain.model.Location
import org.springframework.stereotype.Service
import java.util.*
import kotlin.math.abs

@Service
class BookstoreService(private val bookstores: MutableMap<UUID, Bookstore> = mutableMapOf()) {

    fun createBookstore(name: BookstoreName, location: Location): Bookstore {
        val bookstore = Bookstore(BookstoreId(UUID.randomUUID()), name, location)
        bookstores[bookstore.id.value] = bookstore
        return bookstore
    }

    fun listBookstoresOrderedByProximity(location: Int): List<Bookstore> =
        bookstores.values.sortedBy { abs(it.location.value - location) }

    fun getBookstoreById(bookstoreId: UUID): Bookstore = bookstores[bookstoreId]
        ?: throw BookstoreNotFoundException("Bookstore with ID $bookstoreId not found")

    fun clearBookstores() {
        bookstores.clear()
    }

    class BookstoreNotFoundException(message: String) : RuntimeException(message)
}
