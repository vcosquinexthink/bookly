package com.bookly.acceptance

import com.bookly.catalog.infrastructure.rest.BookDto
import com.bookly.catalog.infrastructure.rest.CreateBookstoreRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.stereotype.Component
import java.util.*

@Component
class StoreTestUtil(@Autowired private val restTemplate: TestRestTemplate) {

    fun createBookstore(dto: BookstoreTestDto): BookstoreTestDto {
        val request = CreateBookstoreRequest(dto.name, dto.location)
        val response = restTemplate.postForEntity(
            "/api/bookstores/bookstores", request, BookstoreTestDto::class.java
        )
        assert(response.statusCode.is2xxSuccessful)
        return response.body!!
    }

    fun stockBook(bookstoreId: UUID, bookDto: BookTestDto, count: Int = 1): String {
        val request = BookDto(bookDto.isbn, bookDto.title, bookDto.author)
        val response = restTemplate.postForEntity(
            "/api/bookstores/bookstores/$bookstoreId/stock?count=$count", request, Void::class.java
        )
        assert(response.statusCode.is2xxSuccessful)
        return bookDto.isbn
    }
}

@Component
class ClientTestUtil(@Autowired private val restTemplate: TestRestTemplate) {
    fun searchBookByISBNNear(isbn: String, location: Int): List<InventoryItemTestDto> {
        val response = restTemplate.getForEntity(
            "/api/public/inventory/search?isbn=$isbn&location=$location",
            Array<InventoryItemTestDto>::class.java
        )
        assert(response.statusCode.is2xxSuccessful)
        return response.body?.filter { it.total > 0 } ?: emptyList()
    }
}
