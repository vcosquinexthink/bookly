package com.bookly.acceptance

import com.bookly.catalog.infrastructure.rest.BookDto
import com.bookly.catalog.infrastructure.rest.BookstoreDto
import com.bookly.catalog.infrastructure.rest.CreateBookstoreRequest
import org.springframework.boot.test.web.client.TestRestTemplate

object BookstoreTestUtil {
    data class BookstoreTestDto(val name: String, val address: String)
    data class BookTestDto(val isbn: String, val title: String, val author: String)

    fun createBookstore(restTemplate: TestRestTemplate, dto: BookstoreTestDto): BookstoreDto {
        val request = CreateBookstoreRequest(dto.name, dto.address)
        val response = restTemplate.postForEntity(
            "/api/bookstores/bookstores",
            request,
            BookstoreDto::class.java
        )
        return response.body!!
    }

    fun stockBook(
        restTemplate: TestRestTemplate,
        bookstoreId: java.util.UUID,
        bookDto: BookTestDto,
        count: Int = 1
    ): String {
        val request = BookDto(bookDto.isbn, bookDto.title, bookDto.author)
        val stockResponse = restTemplate.postForEntity(
            "/api/bookstores/bookstores/$bookstoreId/stock?count=$count",
            request,
            Void::class.java
        )
        assert(stockResponse.statusCode.is2xxSuccessful)
        return bookDto.isbn
    }
}
