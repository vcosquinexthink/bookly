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

@Tag(name = "Bookstore Inventory API", description = "Endpoints for managing inventory within specific bookstores")
interface BookstoreInventoryController {

    @Operation(
        summary = "Get book inventory for a specific book in a bookstore",
        description = "Retrieve detailed inventory information for a specific book (by ISBN) within a particular bookstore, including total stock and available quantity"
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
        summary = "Add stock for a book in a bookstore",
        description = "Increase the inventory stock for a specific book (by ISBN) in a bookstore by the specified quantity. If the book doesn't exist in the bookstore's inventory, it will be added.",
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
        summary = "Get complete inventory for a bookstore",
        description = "Retrieve the full inventory list of all books available in a specific bookstore, showing stock levels and availability for each book"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Bookstore inventory retrieved successfully",
                content = [Content(schema = Schema(implementation = Array<InventoryItemDto>::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Bookstore not found",
                content = [Content(schema = Schema())]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
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
