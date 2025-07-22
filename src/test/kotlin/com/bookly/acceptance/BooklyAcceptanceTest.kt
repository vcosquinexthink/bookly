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
    fun `should retrieve existing inventories ordered by location proximity`() {
        val warAndPeaceBook = BookTestDto("123", "War and peace", "Leon Tolstoi")

        var huelvaBookstore = BookstoreTestDto("Huelva's Literary Haven", HUELVA)
        var zaragozaBookstore = BookstoreTestDto("Zaragoza Page Palace", ZARAGOZA)
        var smallGuadalajaraBookstore = BookstoreTestDto("Guadalajara Tome Tower", GUADALAJARA)

        // given
        huelvaBookstore = bookstoreInteractions.createBookstore(huelvaBookstore)
        bookstoreInteractions.stockBook(huelvaBookstore.id!!, warAndPeaceBook, 3)
        zaragozaBookstore = bookstoreInteractions.createBookstore(zaragozaBookstore)
        bookstoreInteractions.stockBook(zaragozaBookstore.id!!, warAndPeaceBook, 1)
        smallGuadalajaraBookstore = bookstoreInteractions.createBookstore(smallGuadalajaraBookstore)
        bookstoreInteractions.stockBook(smallGuadalajaraBookstore.id!!, warAndPeaceBook, 0)

        // when
        val inventoryResponses = clientInteractions.searchBookByISBNNear("123", GUADALAJARA)

        // then
        assert(inventoryResponses.size == 2) {
            "Expected 2 inventory responses, but got ${inventoryResponses.size}: $inventoryResponses"
        }
        val expected = listOf(
            Triple("123", 3, "Huelva's Literary Haven"),
            Triple("123", 1, "Zaragoza Page Palace")
        )
        expected.forEachIndexed { idx, (isbn, total, name) ->
            val actual = inventoryResponses[idx]
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

    companion object {
        const val GUADALAJARA = 7
        const val ZARAGOZA = 10
        const val HUELVA = 5
    }
}
