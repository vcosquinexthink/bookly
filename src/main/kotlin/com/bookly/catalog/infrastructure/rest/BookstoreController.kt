package com.bookly.catalog.infrastructure.rest

import com.bookly.book.application.BookService
import com.bookly.book.domain.model.Book
import com.bookly.catalog.application.BookstoreService
import com.bookly.catalog.application.InventoryService
import com.bookly.catalog.domain.model.InventoryItem
import com.bookly.catalog.domain.model.valueobject.BookstoreName
import com.bookly.catalog.domain.model.valueobject.Location
import com.bookly.catalog.infrastructure.rest.BookstoreDto.Companion.fromDomain
import com.bookly.catalog.infrastructure.rest.InventoryItemDto.Companion.fromDomain
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/bookstores/bookstores")
class InternalBookstoreControllerImpl(
    private val bookstoreService: BookstoreService,
    private val bookService: BookService,
    private val inventoryService: InventoryService
) : InternalBookstoreController {

    @PostMapping
    override fun createBookstore(
        @RequestBody request: CreateBookstoreRequest
    ): ResponseEntity<BookstoreDto> {
        return ResponseEntity.ok(
            fromDomain(bookstoreService.createBookstore(BookstoreName(request.name), Location(request.location)))
        )
    }

    @GetMapping("/{bookstoreId}/book/{isbn}/stock")
    override fun getBookStock(
        @PathVariable bookstoreId: UUID,
        @PathVariable isbn: String
    ): ResponseEntity<InventoryItemDto> {
        val book = bookService.getBookById(Book.BookId(isbn))
        val bookstore = bookstoreService.getBookstoreById(bookstoreId)
        val inventoryItem: InventoryItem = inventoryService.getInventory(bookstore.id, Book.BookId(isbn));
        return ResponseEntity.ok(fromDomain(inventoryItem, book, bookstore))
    }

    @PostMapping("/{bookstoreId}/book/{isbn}/stock")
    override fun stockBook(
        @PathVariable bookstoreId: UUID,
        @PathVariable isbn: String,
        @RequestParam(required = false, defaultValue = "1") count: Int
    ): ResponseEntity<InventoryItemDto> {
        val book = bookService.getBookById(Book.BookId(isbn))
        val bookstore = bookstoreService.getBookstoreById(bookstoreId)
        val inventoryItem = inventoryService.addInventoryItem(bookstore.id, Book.BookId(isbn), count)
        return ResponseEntity.ok(fromDomain(inventoryItem, book, bookstore))
    }
}