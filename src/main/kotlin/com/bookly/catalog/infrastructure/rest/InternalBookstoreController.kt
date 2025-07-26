package com.bookly.catalog.infrastructure.rest

import com.bookly.catalog.domain.model.Book
import com.bookly.catalog.domain.model.RentalPolicy
import com.bookly.catalog.domain.model.valueobject.BookAuthor
import com.bookly.catalog.domain.model.valueobject.BookId
import com.bookly.catalog.domain.model.valueobject.BookTitle
import com.bookly.catalog.domain.model.valueobject.Price
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import java.math.BigDecimal.ZERO
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
        summary = "Stock a book in a bookstore",
        description = "Specify the number of copies of a book to stock in a bookstore. If the book does not exist, it will be created."
    )
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Book stocked successfully",
            content = [Content(schema = Schema(implementation = String::class))]
        ), ApiResponse(
            responseCode = "400",
            description = "Store not found",
            content = [Content(schema = Schema(implementation = String::class))]
        )]
    )
    fun stockBook(bookstoreId: UUID, bookDto: BookDto, count: Int): ResponseEntity<String>

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
}


data class CreateBookstoreRequest(val name: String, val location: Int)

data class BookstoreDto(val id: UUID, val name: String, val location: Int) {
    companion object {
        fun fromBookstore(bookstore: com.bookly.catalog.domain.model.Bookstore) =
            BookstoreDto(bookstore.id.value, bookstore.name.value, bookstore.location.value)
    }
}

data class BookDto(val isbn: String, val title: String, val author: String) {
    fun toBook(): Book = Book(
        BookId(isbn), BookTitle(title), BookAuthor(author), RentalPolicy(
            Price(ZERO, "USD"), 7
        )
    )

    companion object {
        fun from(book: Book) = BookDto(book.id.isbn, book.title.value, book.author.value)
    }
}

data class InventoryItemDto(val book: BookDto, val total: Int, val available: Int, val bookstore: BookstoreDto) {
    companion object {
        fun fromInventoryItemAndBook(item: com.bookly.catalog.domain.model.InventoryItem, book: Book) = InventoryItemDto(
            BookDto.from(book), item.total, item.available, BookstoreDto.fromBookstore(item.bookstore)
        )
    }
}
