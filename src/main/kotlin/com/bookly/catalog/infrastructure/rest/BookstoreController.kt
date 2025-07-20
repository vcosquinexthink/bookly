package com.bookly.catalog.infrastructure.rest

import com.bookly.catalog.application.BookstoreService
import com.bookly.catalog.domain.model.Book
import com.bookly.catalog.domain.model.valueobject.Location
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/bookstores/bookstores")
class InternalBookstoreController(private val bookstoreService: BookstoreService) {
    @PostMapping
    fun createBookstore(@RequestBody request: CreateBookstoreRequest): BookstoreDto {
        val bookstore = bookstoreService.createBookstore(
            com.bookly.catalog.domain.model.valueobject.BookstoreName(request.name),
            com.bookly.catalog.domain.model.valueobject.Location(request.location)
        )
        return BookstoreDto.from(bookstore)
    }

    @PostMapping("/{bookstoreId}/stock")
    fun stockBook(
        @PathVariable bookstoreId: UUID,
        @RequestBody bookDto: BookDto,
        @RequestParam(required = false, defaultValue = "1") count: Int
    ): ResponseEntity<Unit> {
        bookstoreService.addBook(bookstoreId, bookDto.toBook(), count)
        return ResponseEntity.ok().build()
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
        com.bookly.catalog.domain.model.valueobject.BookId(isbn),
        com.bookly.catalog.domain.model.valueobject.BookTitle(title),
        com.bookly.catalog.domain.model.valueobject.BookAuthor(author),
        com.bookly.catalog.domain.model.RentalPolicy(
            com.bookly.catalog.domain.model.valueobject.Price(java.math.BigDecimal.ZERO, "USD"), 7
        )
    )

    companion object {
        fun from(book: Book) = BookDto(book.id.isbn, book.title.value, book.author.value)
    }
}

data class InventoryItemDto(val book: BookDto, val total: Int, val available: Int) {
    companion object {
        fun from(item: com.bookly.catalog.domain.model.InventoryItem) =
            InventoryItemDto(BookDto.from(item.book), item.total, item.available)
    }
}
