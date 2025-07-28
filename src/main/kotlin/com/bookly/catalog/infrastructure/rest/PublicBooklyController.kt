package com.bookly.catalog.infrastructure.rest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import java.util.*

@Tag(name = "Public API", description = "Public endpoints for searching bookstores and inventory")
interface PublicBooklyController {

    @Operation(
        summary = "Search inventory by ISBN and location",
        description = "Find available copies of a book by ISBN across bookstores, ordered by proximity to the specified location"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Inventory items found successfully",
                content = [Content(schema = Schema(implementation = InventoryItemDto::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Book not found",
                content = [Content(schema = Schema())]
            )
        ]
    )
    fun searchInventoryByIsbnAndLocation(
        @Parameter(description = "ISBN of the book to search for", required = true)
        isbn: String,
        @Parameter(description = "Location code for proximity search", required = true)
        location: Int
    ): ResponseEntity<List<InventoryItemDto>>

    @Operation(
        summary = "Get bookstore catalog",
        description = "Retrieve the complete catalog of books available in a specific bookstore"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Bookstore catalog retrieved successfully",
                content = [Content(schema = Schema(implementation = InventoryItemDto::class))]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error - book reference not found",
                content = [Content(schema = Schema())]
            )
        ]
    )
    fun getBookstoreCatalog(
        @Parameter(description = "Unique identifier of the bookstore", required = true)
        bookstoreId: UUID
    ): ResponseEntity<List<InventoryItemDto>>
}