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
import java.util.*

val bookstoreDto = BookstoreTestDto("CentralBooks", 10)
val bookDto = BookTestDto("9780321125217", "Domain-Driven Design", "Eric Evans")

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BooklyAcceptanceTest {
    @Autowired
    lateinit var restTemplate: TestRestTemplate

    data class AcceptanceBookstoreDto(val id: UUID, val name: String, val location: Int)
    data class AcceptanceInventoryItemDto(val book: AcceptanceBookDto, val total: Int, val available: Int)
    data class AcceptanceBookDto(val isbn: String, val title: String, val author: String)

    @Test
    fun `should search for books available near their location`() {
        val bookstore = createBookstore(restTemplate, bookstoreDto)
        val isbn = stockBook(restTemplate, bookstore.id, bookDto, 3)

        val userLocation = 10
        val bookstoresResponse = restTemplate.getForEntity(
            "/api/public/bookstores",
            Array<AcceptanceBookstoreDto>::class.java
        )
        val nearbyBookstores = bookstoresResponse.body!!.filter { it.location == userLocation }

        // For each nearby bookstore, get inventory for the book
        val inventoryResponses = nearbyBookstores.mapNotNull {
            restTemplate.getForEntity(
                "/api/public/bookstores/${it.id}/inventory/$isbn",
                AcceptanceInventoryItemDto::class.java
            ).body
        }

        // Assert: the inventory is available and correct
        assert(inventoryResponses.isNotEmpty())
        val inventory = inventoryResponses.first()
        assert(inventory.book.title == "Domain-Driven Design")
        assert(inventory.total == 3)
        assert(inventory.available == 3)
    }
}
