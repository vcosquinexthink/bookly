package com.bookly.acceptance

import com.bookly.acceptance.BookstoreTestUtil.BookTestDto
import com.bookly.acceptance.BookstoreTestUtil.BookstoreTestDto
import com.bookly.acceptance.BookstoreTestUtil.createBookstore
import com.bookly.acceptance.BookstoreTestUtil.stockBook
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.ActiveProfiles

val bookstoreDto = BookstoreTestDto("CentralBooks", "MainStreet")
val bookDto = BookTestDto("9780321125217", "Domain-Driven Design", "Eric Evans")

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BooklyAcceptanceTest {
    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun `should search for books available near their location`() {
        val bookstore = createBookstore(restTemplate, bookstoreDto)
        val isbn = stockBook(restTemplate, bookstore.id, bookDto, 3)

        // Action: search for books by location (simulate by listing bookstores and filtering)
        val bookstoresResponse = restTemplate.getForEntity(
            "/api/public/bookstores",
            Array<com.bookly.catalog.infrastructure.rest.BookstoreDto>::class.java
        )
        val nearbyBookstores = bookstoresResponse.body!!.filter { it.address == "MainStreet" }

        // For each nearby bookstore, get inventory for the book
        val inventoryResponses = nearbyBookstores.map {
            restTemplate.getForEntity(
                "/api/public/bookstores/${it.id}/inventory/$isbn",
                com.bookly.catalog.infrastructure.rest.InventoryItemDto::class.java
            ).body
        }.filterNotNull()

        // Assert: the inventory is available and correct
        assert(inventoryResponses.isNotEmpty())
        val inventory = inventoryResponses.first()
        assert(inventory.book.title == "Domain-Driven Design")
        assert(inventory.total == 3)
        assert(inventory.available == 3)
    }
}
