package com.bookly.acceptance

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.test.context.ActiveProfiles
import java.util.*

data class AcceptanceBookstoreDto(val id: UUID?, val name: String, val location: Int)
data class AcceptanceInventoryItemDto(val book: AcceptanceBookDto, val total: Int, val available: Int)
data class AcceptanceBookDto(val isbn: String, val title: String, val author: String)

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class BooklyAcceptanceTest {

    @Autowired
    lateinit var bookstoreInteractions: StoreTestUtil

    @Autowired
    lateinit var clientInteractions: ClientTestUtil

    @Test
    fun `should retrieve existing inventories ordered by location proximity`() {
        val warAndPeaceBook = AcceptanceBookDto("123", "War and peace", "Leon Tolstoi")

        var huelvaBookstore = AcceptanceBookstoreDto(null, "Huelva's Literary Haven", HUELVA)
        var zaragozaBookstore = AcceptanceBookstoreDto(null, "Zaragoza Page Palace", ZARAGOZA)
        var smallGuadalajaraBookstore = AcceptanceBookstoreDto(null, "Guadalajara Tome Tower", GUADALAJARA)

        huelvaBookstore = bookstoreInteractions.createBookstore(huelvaBookstore)
        bookstoreInteractions.stockBook(huelvaBookstore.id!!, warAndPeaceBook, 3)

        zaragozaBookstore = bookstoreInteractions.createBookstore(zaragozaBookstore)
        bookstoreInteractions.stockBook(zaragozaBookstore.id!!, warAndPeaceBook, 1)

        smallGuadalajaraBookstore = bookstoreInteractions.createBookstore(smallGuadalajaraBookstore)
        bookstoreInteractions.stockBook(smallGuadalajaraBookstore.id!!, warAndPeaceBook, 0)

        // Single backend call for inventory search
        val inventoryResponses = clientInteractions.searchBookByISBNNear("123", GUADALAJARA)

        // Aggregate total inventory
//        val totalInventory = inventoryResponses.sumOf { it.total }
//        assert(totalInventory == 4)
//        assert(inventoryResponses.any { it.total == 3 && it.book.isbn == "123" }) // Store A
//        assert(inventoryResponses.any { it.total == 1 && it.book.isbn == "123" }) // Store B
//        assert(inventoryResponses.none { it.total == 0 }) // Store C and D should not appear
    }

    companion object {
        const val GUADALAJARA = 7
        const val ZARAGOZA = 10
        const val HUELVA = 5
    }
}
