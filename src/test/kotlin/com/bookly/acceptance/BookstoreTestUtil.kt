package com.bookly.acceptance

import com.bookly.catalog.infrastructure.rest.BookDto
import com.bookly.catalog.infrastructure.rest.CreateBookstoreRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.util.*

@Component
class StoreTestUtil(@Autowired private val restTemplate: TestRestTemplate) {

    fun createBookstore(dto: BookstoreTestDto): ResponseEntity<BookstoreTestDto> {
        val request = CreateBookstoreRequest(dto.name, dto.location)
        return restTemplate.postForEntity(
            "/api/bookstores/bookstores", request, BookstoreTestDto::class.java
        )
    }

    fun stockBook(bookstoreId: UUID, bookDto: BookTestDto, count: Int = 1): ResponseEntity<String> {
        val request = BookDto(bookDto.isbn, bookDto.title, bookDto.author)
        return restTemplate.postForEntity(
            "/api/bookstores/bookstores/$bookstoreId/stock?count=$count", request, String::class.java
        )
    }

    fun getBookStock(bookstoreId: UUID, isbn: String): ResponseEntity<InventoryItemTestDto> {
        return restTemplate.getForEntity(
            "/api/bookstores/bookstores/$bookstoreId/book/$isbn/stock", InventoryItemTestDto::class.java
        )
    }
}

@Component
class ClientTestUtil(@Autowired private val restTemplate: TestRestTemplate) {
    fun searchBookByISBNNear(isbn: String, location: Int): ResponseEntity<Array<InventoryItemTestDto>> {
        val response = restTemplate.getForEntity(
            "/api/public/inventory/search?isbn=$isbn&location=$location",
            Array<InventoryItemTestDto>::class.java
        )
        return response
    }

    fun searchBookstoresNear(location: Int): ResponseEntity<Array<BookstoreTestDto>> {
        val response = restTemplate.getForEntity(
            "/api/public/bookstores/search?location=$location",
            Array<BookstoreTestDto>::class.java
        )
        return response
    }
}

fun <T> ResponseEntity<T>.assertIs2xxSuccess() {
    assert(this.statusCode.is2xxSuccessful) {
        "Expected HTTP status 2xx, but got ${this.statusCode.value()}"
    }
}

fun <T> ResponseEntity<T>.assertIs4xxError() {
    assert(this.statusCode.is4xxClientError) {
        "Expected HTTP status 4xx, but got ${this.statusCode.value()}"
    }
}
