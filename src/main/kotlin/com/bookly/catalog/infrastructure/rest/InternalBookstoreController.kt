package com.bookly.catalog.infrastructure.rest

import com.bookly.book.domain.model.Book
import com.bookly.book.infrastructure.rest.BookDto
import com.bookly.catalog.domain.model.Bookstore
import com.bookly.catalog.domain.model.InventoryItem
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import java.util.*

@Tag(name = "Bookstores API", description = "Endpoints for managing bookstores and inventory")
interface InternalBookstoreController {

    @Operation(
        summary = "Create a new bookstore", description = "Register a new bookstore into the system"
    )
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Bookstore created successfully",
            content = [Content(schema = Schema(implementation = BookstoreDto::class))]
        )]
    )
    fun createBookstore(request: CreateBookstoreRequest): ResponseEntity<BookstoreDto>

    @Operation(
        summary = "Get book stock information",
        description = "Retrieve inventory information for a specific book in a bookstore"
    )
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Book stock retrieved successfully",
            content = [Content(schema = Schema(implementation = InventoryItemDto::class))]
        ), ApiResponse(
            responseCode = "404",
            description = "Bookstore or book not found",
            content = [Content(schema = Schema())]
        )]
    )
    fun getBookStock(bookstoreId: UUID, isbn: String): ResponseEntity<InventoryItemDto>

    @Operation(
        summary = "Stock a book in a bookstore",
        description = "Adds stock for a book in the specified bookstore.",
        requestBody = RequestBody(
            description = "Number of units to stock (default: 1)",
            required = false,
            content = [Content(schema = Schema(type = "integer", defaultValue = "1"))]
        ),
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Book stocked successfully",
                content = [Content(schema = Schema(implementation = InventoryItemDto::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Bookstore or book not found",
                content = [Content(schema = Schema())]
            )
        ]
    )
    fun stockBook(bookstoreId: UUID, isbn: String, count: Int): ResponseEntity<InventoryItemDto>
}


data class CreateBookstoreRequest(val name: String, val location: Int)

data class BookstoreDto(val id: UUID, val name: String, val location: Int) {
    companion object {
        fun fromDomain(bookstore: Bookstore) =
            BookstoreDto(bookstore.id.value, bookstore.name.value, bookstore.location.value)
    }
}

data class InventoryItemDto(val book: BookDto, val total: Int, val available: Int, val bookstore: BookstoreDto) {
    companion object {
        fun fromDomain(item: InventoryItem, book: Book, bookstore: Bookstore) = InventoryItemDto(
            BookDto.fromDomain(book), item.total, item.available, BookstoreDto.fromDomain(bookstore)
        )
    }
}
