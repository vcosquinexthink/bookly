package com.bookly.catalog.infrastructure.rest

import com.bookly.catalog.application.BookService
import com.bookly.catalog.application.BookstoreService
import com.bookly.catalog.domain.model.valueobject.BookId
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/public")
@Tag(name = "Public API", description = "Public endpoints for searching bookstores and inventory")
class PublicBooklyController(
    private val bookstoreService: BookstoreService,
    private val bookService: BookService
) {

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
    @GetMapping("/inventory/search")
    fun searchInventoryByIsbnAndLocation(
        @Parameter(description = "ISBN of the book to search for", required = true)
        @RequestParam isbn: String,
        @Parameter(description = "Location code for proximity search", required = true)
        @RequestParam location: Int
    ): ResponseEntity<List<InventoryItemDto>> {
        val book = bookService.getBookById(BookId(isbn)) ?: return ResponseEntity.notFound().build()
        val bookstoresByProximity = bookstoreService.listBookstoresOrderedByProximity(location)
        val inventoryItems = bookstoresByProximity.mapNotNull {
            val inventoryItem = it.getInventoryForBook(BookId(isbn))
            inventoryItem?.let { item ->
                InventoryItemDto.from(item, book)
            }
        }.filter { it.total > 0 }
        return ResponseEntity.ok(inventoryItems)
    }

    @Operation(
        summary = "Search bookstores by proximity",
        description = "Get all bookstores ordered by proximity to the specified location"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Bookstores retrieved successfully",
                content = [Content(schema = Schema(implementation = BookstoreDto::class))]
            )
        ]
    )
    @GetMapping("/bookstores/search")
    fun searchBookstoresByProximity(
        @Parameter(description = "Location code for proximity search", required = true)
        @RequestParam location: Int
    ): ResponseEntity<List<BookstoreDto>> {
        val bookstores = bookstoreService.listBookstoresOrderedByProximity(location)
        val response = bookstores.map { BookstoreDto.from(it) }
        return ResponseEntity.ok(response)
    }


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
    @GetMapping("/bookstores/{storeId}/catalog")
    fun getBookstoreCatalog(
        @Parameter(description = "Unique identifier of the bookstore", required = true)
        @PathVariable storeId: UUID
    ): ResponseEntity<List<InventoryItemDto>> {
        val catalog = bookstoreService.getCatalogByBookstoreId(storeId)
        val response = catalog.map { item ->
            val book = bookService.getBookById(item.bookId)
                ?: throw IllegalStateException("Book with ID ${item.bookId} not found")
            InventoryItemDto.from(item, book)
        }
        return ResponseEntity.ok(response)
    }
}
