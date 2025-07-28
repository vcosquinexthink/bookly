package com.bookly.inventory.infrastructure.rest

import com.bookly.book.domain.model.Book
import com.bookly.book.infrastructure.rest.BookDto
import com.bookly.bookstore.domain.model.Bookstore
import com.bookly.bookstore.infrastructure.rest.BookstoreDto
import com.bookly.inventory.domain.model.InventoryItem
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import java.util.*

@Tag(name = "Inventory API", description = "Endpoints for retrieving and managing inventory")
interface InventoryController {

    @Operation(
        summary = "Get book inventory information",
        description = "Retrieve inventory information for a specific book in a bookstore"
    )
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Book inventory retrieved successfully",
            content = [Content(schema = Schema(implementation = InventoryItemDto::class))]
        ), ApiResponse(
            responseCode = "404",
            description = "Bookstore or book not found",
            content = [Content(schema = Schema())]
        )]
    )
    fun getBookInventory(bookstoreId: UUID, isbn: String): ResponseEntity<InventoryItemDto>

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
    fun updateInventory(bookstoreId: UUID, isbn: String, count: Int): ResponseEntity<InventoryItemDto>


    @Operation(
        summary = "Get bookstore inventory",
        description = "Retrieve the complete inventory of books available in a specific bookstore"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Bookstore inventory retrieved successfully",
                content = [Content(schema = Schema(implementation = InventoryItemDto::class))]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error - book reference not found",
                content = [Content(schema = Schema())]
            )
        ]
    )
    fun getBookstoreInventory(
        @Parameter(description = "Unique identifier of the bookstore", required = true)
        bookstoreId: UUID
    ): ResponseEntity<List<InventoryItemDto>>
}

data class InventoryItemDto(val book: BookDto, val total: Int, val available: Int, val bookstore: BookstoreDto) {
    companion object {
        fun fromDomain(item: InventoryItem, book: Book, bookstore: Bookstore) = InventoryItemDto(
            BookDto.fromDomain(book), item.total, item.available, BookstoreDto.fromDomain(bookstore)
        )
    }
}
