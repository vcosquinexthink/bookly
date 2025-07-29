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
class ClientInventoryAcceptanceTest {

    @Autowired
    lateinit var interactions: TestUtil

    @Autowired
    lateinit var bookstoreService: BookstoreService

    val warAndPeaceBook = BookTestDto("123", "War and peace", "Leon Tolstoi")
    val pachinkoBook = BookTestDto("998", "Pachinko", "Min Jin Lee")
    val antsBook = BookTestDto("210", "Of Ants and Dinosaurs", "Liu Cixin")

    var huelvaBookstore = BookstoreTestDto("Huelva's Literary Haven", HUELVA)
    var zaragozaBookstore = BookstoreTestDto("Zaragoza Page Palace", ZARAGOZA)
    var smallGuadalajaraBookstore = BookstoreTestDto("Guadalajara Tome Tower", GUADALAJARA)

    @BeforeEach
    fun setup() {
        bookstoreService.clearBookstores() // todo: remove
        interactions.createBook(warAndPeaceBook)
        interactions.createBook(pachinkoBook)
        interactions.createBook(antsBook)
        huelvaBookstore = interactions.createBookstore(huelvaBookstore).body!!
        zaragozaBookstore = interactions.createBookstore(zaragozaBookstore).body!!
        smallGuadalajaraBookstore = interactions.createBookstore(smallGuadalajaraBookstore).body!!
        interactions.incrementInventory(huelvaBookstore, warAndPeaceBook, 3)
        interactions.incrementInventory(huelvaBookstore, pachinkoBook, 2)
        interactions.incrementInventory(huelvaBookstore, antsBook, 1)
        interactions.incrementInventory(zaragozaBookstore, warAndPeaceBook, 1)
        interactions.incrementInventory(smallGuadalajaraBookstore, warAndPeaceBook, 0)
    }

    @Test
    fun `clients can search for book inventories ordered by location proximity`() {
        // when
        val response = interactions.searchBookByISBNNear("123", GUADALAJARA)

        // then
        assert(response.statusCode.is2xxSuccessful) {
            "Expected HTTP status 2xx, but got ${response.statusCode.value()}"
        }
        val inventoryItems = response.body!!
        assert(inventoryItems.size == 3) {
            "Expected 2 inventory responses, but got ${inventoryItems.size}: $inventoryItems"
        }
        val expected = listOf(
            Triple("123", 0, "Guadalajara Tome Tower"),
            Triple("123", 3, "Huelva's Literary Haven"),
            Triple("123", 1, "Zaragoza Page Palace"),
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
    fun `clients can browse a bookstore inventory`() {
        // when
        val response = interactions.getBookstoreInventory(huelvaBookstore.id!!)

        // then
        assert(response.statusCode.is2xxSuccessful) {
            "Expected HTTP status 2xx, but got ${response.statusCode.value()}"
        }
        assert(response.body!!.size == 3) {
            "Expected 3 books in inventory, but got ${response.body!!.size}: ${response.body!!}"
        }
        assert(response.body!!.any { it.book.isbn == warAndPeaceBook.isbn && it.total == 3 }) {
            "Expected book '${warAndPeaceBook.isbn}' with total 3, but it was not found or had a different total."
        }
        assert(response.body!!.any { it.book.isbn == pachinkoBook.isbn && it.total == 2 }) {
            "Expected book '${pachinkoBook.isbn}' with total 100, but it was not found or had a different total."
        }
        assert(response.body!!.any { it.book.isbn == antsBook.isbn && it.total == 1 }) {
            "Expected book '${antsBook.isbn}' with total 0, but it was not found or had a different total."
        }
    }

    companion object {
        const val GUADALAJARA = 7
        const val ZARAGOZA = 10
        const val HUELVA = 5
    }
}
