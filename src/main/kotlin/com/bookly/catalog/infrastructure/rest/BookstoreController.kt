package com.bookly.catalog.infrastructure.rest

import com.bookly.catalog.application.BookService
import com.bookly.catalog.application.BookstoreService
import com.bookly.catalog.domain.model.valueobject.BookId
import com.bookly.catalog.domain.model.valueobject.BookstoreName
import com.bookly.catalog.domain.model.valueobject.Location
import com.bookly.catalog.infrastructure.rest.BookstoreDto.Companion.fromBookstore
import com.bookly.catalog.infrastructure.rest.InventoryItemDto.Companion.fromInventoryItemAndBook
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/bookstores/bookstores")
class InternalBookstoreControllerImpl(
    private val bookstoreService: BookstoreService, private val bookService: BookService
) : InternalBookstoreController {

    @PostMapping
    override fun createBookstore(@RequestBody request: CreateBookstoreRequest): ResponseEntity<BookstoreDto> {
        return ResponseEntity.ok(
            fromBookstore(
                bookstoreService.createBookstore(
                    BookstoreName(request.name), Location(request.location)
                )
            )
        )
    }

    @PostMapping("/{bookstoreId}/stock")
    override fun stockBook(
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
    override fun getBookStock(
        @PathVariable bookstoreId: UUID, @PathVariable isbn: String
    ): ResponseEntity<InventoryItemDto> {
        val bookstore = bookstoreService.getBookstoreById(bookstoreId)
        // todo: throw exception if not found
        val inventoryItem = bookstore.getInventoryForBookId(BookId(isbn)) ?: return ResponseEntity.notFound().build()
        val book = bookService.getBookById(BookId(isbn)) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(fromInventoryItemAndBook(inventoryItem, book))
    }
}