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
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "Public Inventory Search API", description = "Public endpoints for searching book availability across all bookstores")
interface InventoryController {

    @Operation(
        summary = "Search for book availability by ISBN and location",
        description = "Find all available copies of a specific book (by ISBN) across all bookstores in the system, ordered by proximity to the specified location. Returns inventory details including stock levels and bookstore information."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Inventory search completed successfully. Returns list of available inventory items ordered by proximity.",
                content = [Content(schema = Schema(implementation = Array<InventoryItemDto>::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "No book found with the specified ISBN",
                content = [Content(schema = Schema())]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid ISBN format or location parameter",
                content = [Content(schema = Schema())]
            )
        ]
    )
    fun searchInventoryByIsbnAndLocation(
        @Parameter(description = "ISBN-13 or ISBN-10 of the book to search for", required = true, example = "978-0134685991")
        isbn: String,
        @Parameter(description = "Location code for proximity-based ordering of results", required = true, example = "12345")
        location: Int
    ): ResponseEntity<List<InventoryItemDto>>
}