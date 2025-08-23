package com.bookly.acceptance

import com.bookly.bookstore.application.BookstoreService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_METHOD
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import java.util.*

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestInstance(PER_METHOD)
class ClientBookReservationAcceptanceTest {

    @Autowired
    lateinit var client: TestUtil

    @Autowired
    lateinit var bookstoreService: BookstoreService

    val warAndPeaceBook = BookTestDto("123", "War and peace", "Leon Tolstoi")
    var huelvaBookstore = BookstoreTestDto("Huelva's Literary Haven", 1, UUID.randomUUID())
    var client1 = ClientTestDto(UUID.randomUUID())

    @BeforeEach
    fun setup() {
        bookstoreService.clearBookstores() // todo: remove
        client.createBook(warAndPeaceBook)
        huelvaBookstore = client.createBookstore(huelvaBookstore).body!!
        client.incrementInventory(huelvaBookstore, warAndPeaceBook, 3)
    }

    @Test
    fun `clients reserve an available book`() {
        // todo: add reservation details, user, reservation comments, etc.
        client.reserveBook(huelvaBookstore, warAndPeaceBook, client1).apply {
            assert(statusCode.is2xxSuccessful)
            val reservation = body ?: throw AssertionError("Reservation body is null")
        }
        client.getBookInventory(huelvaBookstore, warAndPeaceBook).apply {
            assert(statusCode.is2xxSuccessful)
            val inventory = body ?: throw AssertionError("Inventory body is null")
            assert(inventory.total == 3) { "Expected 3 total books in inventory, but found ${inventory.total}" }
            assert(inventory.available == 2) { "Expected 2 available books in inventory, but found ${inventory.available}" }
        }
    }

    @Test
    fun `clients can claim a reserved book`() {


        // todo: implement this test (remove reservation and decrease total)
        // /bookly/bookstores/${bookstore.id!!}/book/${book.isbn}/reserve/${reservationId}/claim
        // /bookly/reservations/${reservationId}/claim
    }
}
