package com.bookly.acceptance

import com.bookly.catalog.application.BookstoreService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_METHOD
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import java.util.*

data class BookstoreTestDto(val name: String, val location: Int, val id: UUID? = null)
data class InventoryItemTestDto(
    val book: BookTestDto,
    val total: Int,
    val available: Int,
    val bookstore: BookstoreTestDto
)
data class BookstoreInventoryItemTestDto(
    val book: BookTestDto,
    val total: Int,
    val available: Int
)

data class BookTestDto(val isbn: String, val title: String, val author: String)

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestInstance(PER_METHOD)
class ClientAcceptanceTest {

    @Autowired
    lateinit var bookstoreInteractions: StoreTestUtil

    @Autowired
    lateinit var clientInteractions: ClientTestUtil

    @Autowired
    lateinit var bookstoreService: BookstoreService

    @BeforeEach
    fun setup() {
        bookstoreService.clearBookstores()
    }

    @Test
    fun `clients can search for book inventories ordered by location proximity`() {
        val warAndPeaceBook = BookTestDto("123", "War and peace", "Leon Tolstoi")

        var huelvaBookstore = BookstoreTestDto("Huelva's Literary Haven", HUELVA)
        var zaragozaBookstore = BookstoreTestDto("Zaragoza Page Palace", ZARAGOZA)
        var smallGuadalajaraBookstore = BookstoreTestDto("Guadalajara Tome Tower", GUADALAJARA)

        // given
        huelvaBookstore = bookstoreInteractions.createBookstore(huelvaBookstore).body!!
        bookstoreInteractions.stockBook(huelvaBookstore.id!!, warAndPeaceBook, 3)
        zaragozaBookstore = bookstoreInteractions.createBookstore(zaragozaBookstore).body!!
        bookstoreInteractions.stockBook(zaragozaBookstore.id!!, warAndPeaceBook, 1)
        smallGuadalajaraBookstore = bookstoreInteractions.createBookstore(smallGuadalajaraBookstore).body!!
        bookstoreInteractions.stockBook(smallGuadalajaraBookstore.id!!, warAndPeaceBook, 0)

        // when
        val response = clientInteractions.searchBookByISBNNear("123", GUADALAJARA)

        // then
        assert(response.statusCode.is2xxSuccessful) {
            "Expected HTTP status 2xx, but got ${response.statusCode.value()}"
        }
        val inventoryItems = response.body!!
        assert(inventoryItems.size == 2) {
            "Expected 2 inventory responses, but got ${inventoryItems.size}: $inventoryItems"
        }
        val expected = listOf(
            Triple("123", 3, "Huelva's Literary Haven"),
            Triple("123", 1, "Zaragoza Page Palace")
        )
        expected.forEachIndexed { idx, (isbn, total, name) ->
            val actual = inventoryItems[idx]
            assert(actual.book.isbn == isbn) {
                "Expected isbn $isbn at index $idx, but got ${actual.book.isbn}"
            }
            assert(actual.total == total) {
                "Expected total $total at index $idx, but got ${actual.total}"
            }
            assert(actual.bookstore.name == name) {
                "Expected bookstore name $name at index $idx, but got ${actual.bookstore.name}"
            }
        }
    }

    @Test
    fun `clients can search for bookstores ordered by location proximity`() {
        val huelvaBookstore = BookstoreTestDto("Huelva's Literary Haven", HUELVA)
        val zaragozaBookstore = BookstoreTestDto("Zaragoza Page Palace", ZARAGOZA)
        val smallGuadalajaraBookstore = BookstoreTestDto("Guadalajara Tome Tower", GUADALAJARA)

        // given
        bookstoreInteractions.createBookstore(huelvaBookstore)
        bookstoreInteractions.createBookstore(zaragozaBookstore)
        bookstoreInteractions.createBookstore(smallGuadalajaraBookstore)

        // when
        val response = clientInteractions.searchBookstoresNear(GUADALAJARA)

        // then
        assert(response.statusCode.is2xxSuccessful) {
            "Expected HTTP status 2xx, but got ${response.statusCode.value()}"
        }
        val bookstores = response.body!!
        assert(bookstores.size == 3) {
            "Expected 3 bookstores, but got ${bookstores.size}: $bookstores"
        }
        val expectedOrder = listOf(
            smallGuadalajaraBookstore.name,
            huelvaBookstore.name,
            zaragozaBookstore.name
        )
        expectedOrder.forEachIndexed { idx, expectedName ->
            assert(bookstores[idx].name == expectedName) {
                "Expected bookstore at index $idx to be $expectedName, but got ${bookstores[idx].name}"
            }
        }
    }

    @Test
    fun `clients can browse a bookstore catalog`() {
        // given
        val warAndPeaceBook = BookTestDto("123", "War and peace", "Leon Tolstoi")
        val pachinkoBook = BookTestDto("998", "Pachinko", "Min Jin Lee")
        val antsBook = BookTestDto("210", "Of Ants and Dinosaurs", "Liu Cixin")
        var huelvaBookstore = BookstoreTestDto("Huelva's Literary Haven", HUELVA)
        huelvaBookstore = bookstoreInteractions.createBookstore(huelvaBookstore).body!!
        bookstoreInteractions.stockBook(huelvaBookstore.id!!, warAndPeaceBook, 3)
        bookstoreInteractions.stockBook(huelvaBookstore.id!!, pachinkoBook, 100)
        bookstoreInteractions.stockBook(huelvaBookstore.id!!, antsBook, 0)


        // when
        val response = clientInteractions.getBookstoreCatalog(huelvaBookstore.id!!)

        // then
        assert(response.statusCode.is2xxSuccessful) {
            "Expected HTTP status 2xx, but got ${response.statusCode.value()}"
        }
        assert(response.body!!.size == 3) {
            "Expected 3 books in catalog, but got ${response.body!!.size}: ${response.body!!}"
        }
        assert(response.body!!.any { it.book.isbn == warAndPeaceBook.isbn && it.total == 3 }) {
            "Expected book '${warAndPeaceBook.isbn}' with total 3, but it was not found or had a different total."
        }
        assert(response.body!!.any { it.book.isbn == pachinkoBook.isbn && it.total == 100 }) {
            "Expected book '${pachinkoBook.isbn}' with total 100, but it was not found or had a different total."
        }
        assert(response.body!!.any { it.book.isbn == antsBook.isbn && it.total == 0 }) {
            "Expected book '${antsBook.isbn}' with total 0, but it was not found or had a different total."
        }
    }

    companion object {
        const val GUADALAJARA = 7
        const val ZARAGOZA = 10
        const val HUELVA = 5
    }
}
