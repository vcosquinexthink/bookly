package com.bookly.acceptance

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.test.context.ActiveProfiles
import java.util.*

data class BookstoreTestDto(val name: String, val location: Int, val id: UUID? = null)
data class InventoryItemTestDto(
    val book: BookTestDto,
    val total: Int,
    val available: Int,
    val bookstore: BookstoreTestDto
)

data class BookTestDto(val isbn: String, val title: String, val author: String)

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class BooklyAcceptanceTest {

    @Autowired
    lateinit var bookstoreInteractions: StoreTestUtil

    @Autowired
    lateinit var clientInteractions: ClientTestUtil

    @Test
    fun `response`() {
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
    fun `should return 400 with error message when adding book to non-existent bookstore`() {
        val nonExistentBookstoreId = UUID.randomUUID()
        val warAndPeaceBook = BookTestDto("123", "War and peace", "Leon Tolstoi")

        val response = bookstoreInteractions.stockBook(nonExistentBookstoreId, warAndPeaceBook, 3)

        assert(response.statusCode.is4xxClientError)
        assert(response.body == "Bookstore with ID $nonExistentBookstoreId not found")
    }

    companion object {
        const val GUADALAJARA = 7
        const val ZARAGOZA = 10
        const val HUELVA = 5
    }
}
