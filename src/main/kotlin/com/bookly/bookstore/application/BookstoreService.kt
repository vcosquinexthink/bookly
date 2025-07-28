package com.bookly.bookstore.application

import com.bookly.bookstore.domain.model.Bookstore
import com.bookly.bookstore.domain.model.BookstoreId
import com.bookly.bookstore.domain.model.BookstoreName
import com.bookly.bookstore.domain.model.Location
import org.springframework.stereotype.Service
import java.util.*
import kotlin.math.abs

@Service
class BookstoreService(private val bookstores: MutableList<Bookstore> = mutableListOf()) { //todo: replace with map

    fun createBookstore(name: BookstoreName, location: Location): Bookstore {
        val bookstore = Bookstore(BookstoreId(UUID.randomUUID()), name, location)
        bookstores.add(bookstore)
        return bookstore
    }

    fun listBookstoresOrderedByProximity(location: Int): List<Bookstore> =
        bookstores.sortedBy { abs(it.location.value - location) }

    fun getBookstoreById(bookstoreId: UUID): Bookstore = bookstores.find { it.id.value == bookstoreId }
        ?: throw BookstoreNotFoundException("Bookstore with ID $bookstoreId not found")

    fun clearBookstores() {
        bookstores.clear()
    }

    class BookstoreNotFoundException(message: String) : RuntimeException(message)
}
