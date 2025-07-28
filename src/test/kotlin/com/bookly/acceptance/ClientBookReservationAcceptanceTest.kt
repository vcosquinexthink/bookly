package com.bookly.acceptance

import com.bookly.bookstore.application.BookstoreService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_METHOD
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestInstance(PER_METHOD)
class ClientBookReservationAcceptanceTest {

    @Autowired
    lateinit var interactions: TestUtil

    @Autowired
    lateinit var bookstoreService: BookstoreService

    val warAndPeaceBook = BookTestDto("123", "War and peace", "Leon Tolstoi")
    var huelvaBookstore = BookstoreTestDto("Huelva's Literary Haven", 1)

    @BeforeEach
    fun setup() {
        bookstoreService.clearBookstores() // todo: remove
        interactions.createBook(warAndPeaceBook)
        huelvaBookstore = interactions.createBookstore(huelvaBookstore).body!!
        interactions.updateInventory(huelvaBookstore.id!!, warAndPeaceBook.isbn, 3)
    }

    @Test
    fun `clients reserve an available book`() {
        interactions.reserveBook(huelvaBookstore.id!!, warAndPeaceBook.isbn).apply {
            assert(statusCode.is2xxSuccessful)
            val reservation = body ?: throw AssertionError("Reservation body is null")
        }
        interactions.getBookInventory(huelvaBookstore.id!!, warAndPeaceBook.isbn).apply {
            assert(statusCode.is2xxSuccessful)
            val inventory = body ?: throw AssertionError("Inventory body is null")
            assert(inventory.total == 3) { "Expected 3 total books in inventory, but found ${inventory.total}" }
            // todo: assert(inventory.available == 2) { "Expected 2 available books in inventory, but found ${inventory.available}" }
        }
    }

    @Test
    fun `clients cant reserve a book with no availability`() {
        // todo: implement this test (400 error)
    }

    @Test
    fun `clients can claim a reserved book`() {
        // todo: implement this test (remove reservation and decrease total)
    }
}
