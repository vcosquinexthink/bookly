package com.bookly.catalog.infrastructure.rest

import com.bookly.catalog.application.BookService
import com.bookly.catalog.application.BookstoreService
import com.bookly.catalog.domain.model.Book
import com.bookly.catalog.domain.model.RentalPolicy
import com.bookly.catalog.domain.model.valueobject.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal.ZERO
import java.util.*

@RestController
@RequestMapping("/api/bookstores/bookstores")
class InternalBookstoreController(
    private val bookstoreService: BookstoreService, private val bookService: BookService
) {

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
    @PostMapping
    fun createBookstore(@RequestBody request: CreateBookstoreRequest): ResponseEntity<BookstoreDto> {
        val response = BookstoreDto.from(
            bookstoreService.createBookstore(
                BookstoreName(request.name), Location(request.location)
            )
        )
        return ResponseEntity.ok(response)
    }

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
    @PostMapping("/{bookstoreId}/stock")
    fun stockBook(
        @PathVariable bookstoreId: UUID,
        @RequestBody bookDto: BookDto,
        @RequestParam(required = false, defaultValue = "1") count: Int
    ): ResponseEntity<String> { // todo: change to InventoryItemDto body
        val book = bookDto.toBook()
        bookService.addOrUpdateBookReference(book)
        bookstoreService.addBook(bookstoreId, book.id, count)
        return ResponseEntity.ok("Book successfully stocked.")
    }

    @GetMapping("/{bookstoreId}/book/{isbn}/stock")
    fun getBookStock(
        @PathVariable bookstoreId: UUID, @PathVariable isbn: String
    ): ResponseEntity<InventoryItemDto> {
        val bookstore = bookstoreService.getBookstoreById(bookstoreId)

        val inventoryItem = bookstore.getInventoryForBook(BookId(isbn)) ?: return ResponseEntity.notFound().build()

        val book = bookService.getBookById(BookId(isbn)) ?: return ResponseEntity.notFound().build()

        val response = InventoryItemDto.from(inventoryItem, book)
        return ResponseEntity.ok(response)
    }
}

data class CreateBookstoreRequest(val name: String, val location: Int)

data class BookstoreDto(val id: UUID, val name: String, val location: Int) {
    companion object {
        fun from(bookstore: com.bookly.catalog.domain.model.Bookstore) =
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
        fun from(item: com.bookly.catalog.domain.model.InventoryItem, book: Book) = InventoryItemDto(
            BookDto.from(book), item.total, item.available, BookstoreDto.from(item.bookstore)
        )
    }
}
