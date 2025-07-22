package com.bookly.catalog.application

import com.bookly.catalog.domain.model.Bookstore
import com.bookly.catalog.domain.model.valueobject.BookstoreName
import com.bookly.catalog.domain.model.valueobject.Location
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class BookstoreServiceTest {
    @Test
    fun `should return bookstores ordered by proximity to given location`() {
        val service = BookstoreService(mutableListOf())
        val b1 = service.createBookstore(BookstoreName("A"), Location(10))
        val b2 = service.createBookstore(BookstoreName("B"), Location(5))
        val b3 = service.createBookstore(BookstoreName("C"), Location(7))
        val b4 = service.createBookstore(BookstoreName("D"), Location(20))

        val ordered = service.listBookstoresOrderedByProximity(7)
        val expected = listOf(b3, b2, b1, b4) // 7, 5, 10, 20
        assertEquals(
            expected.map { it.name.value },
            ordered.map { it.name.value },
            "Expected order: ${expected.map { it.name.value }}, but got: ${ordered.map { it.name.value }}"
        )
    }
}
