package com.bookly.acceptance

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.test.context.ActiveProfiles
import java.util.UUID.randomUUID

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class BookstoreAcceptanceTest {

    @Autowired
    lateinit var bookstoreInteractions: StoreTestUtil

    @Test
    fun `bookstores can be registered`() {
        var bookstore = BookstoreTestDto("Freshair Bookstore", 0)
        // when then
        bookstoreInteractions.createBookstore(bookstore).also { it.assertIs2xxSuccess() }
    }

    @Test
    fun `bookstores can publish and query inventories`() {
        val book = BookTestDto("123", "War and peace", "Leon Tolstoi")
        var bookstore = BookstoreTestDto("Freshair Bookstore", 0)
        // when
        bookstore = bookstoreInteractions.createBookstore(bookstore).body!!
        bookstoreInteractions.stockBook(bookstore.id!!, book, 3)
        // then
        val inventory = bookstoreInteractions.getBookStock(bookstore.id!!, "123")
            .also { it.assertIs2xxSuccess() }.body!!
        assert(inventory.bookstore.name == "Freshair Bookstore") {
            "Expected bookstore name to be 'Freshair Bookstore', but got '${inventory.bookstore.name}'"
        }
        assert(inventory.book.isbn == "123") {
            "Expected book ISBN to be '123', but got '${inventory.book.isbn}'"
        }
        assert(inventory.total == 3) {
            "Expected total to be 0, but got ${inventory.total}"
        }
    }

    @Test
    fun `should return 400 with error message when adding book to non-existent bookstore`() {
        val nonExistentBookstoreId = randomUUID()
        val warAndPeaceBook = BookTestDto("123", "War and peace", "Leon Tolstoi")

        bookstoreInteractions.stockBook(nonExistentBookstoreId, warAndPeaceBook, 3)
            .also { it.assertIs4xxError() }
            .also { assert(it.body == "Bookstore with ID $nonExistentBookstoreId not found") }
    }
}
