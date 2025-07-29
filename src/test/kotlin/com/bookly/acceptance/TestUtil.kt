package com.bookly.acceptance

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
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

data class CreateBookstoreRequestDto(val name: String, val location: Int)

class BooksReservationDto(val reservationId: UUID)

@Component
class TestUtil(@Autowired private val restTemplate: TestRestTemplate) {

    fun createBook(bookDto: BookTestDto): ResponseEntity<BookTestDto> {
        return restTemplate.postForEntity("/bookly/books", bookDto, BookTestDto::class.java)
    }

    fun createBookstore(dto: BookstoreTestDto): ResponseEntity<BookstoreTestDto> {
        val request = CreateBookstoreRequestDto(dto.name, dto.location)
        return restTemplate.postForEntity(
            "/bookly/bookstores", request, BookstoreTestDto::class.java
        )
    }

    fun getBookInventory(bookstoreId: UUID, isbn: String): ResponseEntity<InventoryItemTestDto> {
        return restTemplate.getForEntity(
            "/bookly/bookstores/$bookstoreId/book/$isbn/inventory", InventoryItemTestDto::class.java
        )
    }

    fun updateInventory(bookstoreId: UUID, isbn: String, count: Int = 1): ResponseEntity<InventoryItemTestDto> {
        return restTemplate.postForEntity(
            "/bookly/bookstores/$bookstoreId/book/$isbn/inventory/increment?count=$count",
            null,
            InventoryItemTestDto::class.java
        )
    }

    fun searchBookByISBNNear(isbn: String, location: Int): ResponseEntity<Array<InventoryItemTestDto>> {
        val response = restTemplate.getForEntity(
            "/bookly/inventory/search?isbn=$isbn&location=$location",
            Array<InventoryItemTestDto>::class.java
        )
        return response
    }

    fun searchBookstoresNear(location: Int): ResponseEntity<Array<BookstoreTestDto>> {
        val response = restTemplate.getForEntity(
            "/bookly/bookstores/search?location=$location",
            Array<BookstoreTestDto>::class.java
        )
        return response
    }

    fun getBookstoreInventory(storeId: UUID): ResponseEntity<Array<BookstoreInventoryItemTestDto>> {
        val response = restTemplate.getForEntity(
            "/bookly/bookstores/$storeId/inventory",
            Array<BookstoreInventoryItemTestDto>::class.java
        )
        return response
    }

    fun reserveBook(bookstoreId: UUID, isbn: String): ResponseEntity<BooksReservationDto> {
        val response = restTemplate.postForEntity(
            "/bookly/bookstores/$bookstoreId/book/$isbn/reserve",
            null,
            BooksReservationDto::class.java
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
