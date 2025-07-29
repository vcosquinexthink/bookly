package com.bookly.inventory.infrastructure.rest

import com.bookly.book.application.BookService
import com.bookly.book.domain.model.Book
import com.bookly.book.infrastructure.rest.BookDto
import com.bookly.bookstore.application.BookstoreService
import com.bookly.bookstore.domain.model.BookstoreId
import com.bookly.bookstore.infrastructure.rest.BookstoreDto
import com.bookly.inventory.application.InventoryService
import com.bookly.inventory.domain.model.InventoryItem
import com.bookly.inventory.infrastructure.rest.InventoryItemDto.Companion.fromDomain
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/bookly/bookstores")
class BookstoreInventoryControllerImpl(
    private val bookstoreService: BookstoreService,
    private val bookService: BookService,
    private val inventoryService: InventoryService
) : BookstoreInventoryController {

    @GetMapping("/{bookstoreId}/book/{isbn}/inventory")
    override fun getBookInventory(
        @PathVariable bookstoreId: UUID,
        @PathVariable isbn: String
    ): ResponseEntity<InventoryItemDto> {
        val book = bookService.getBookById(Book.BookId(isbn))
        val bookstore = bookstoreService.getBookstoreById(bookstoreId)
        val inventoryItem: InventoryItem = inventoryService.getInventory(bookstore.id, Book.BookId(isbn));
        return ResponseEntity.ok(fromDomain(inventoryItem, book, bookstore))
    }

    @PostMapping("/{bookstoreId}/book/{isbn}/inventory/increment")
    override fun updateInventory(
        @PathVariable bookstoreId: UUID,
        @PathVariable isbn: String,
        @RequestParam(required = false, defaultValue = "1") count: Int
    ): ResponseEntity<InventoryItemDto> {
        val book = bookService.getBookById(Book.BookId(isbn))
        val bookstore = bookstoreService.getBookstoreById(bookstoreId)
        val inventoryItem = inventoryService.addInventoryItem(bookstore.id, Book.BookId(isbn), count)
        return ResponseEntity.ok(fromDomain(inventoryItem, book, bookstore))
    }

    @GetMapping("/{bookstoreId}/inventory")
    override fun getBookstoreInventory(
        @PathVariable bookstoreId: UUID
    ): ResponseEntity<List<InventoryItemDto>> {
        val bookStore = bookstoreService.getBookstoreById(bookstoreId)
        return ResponseEntity.ok(
            inventoryService.getInventoriesForBookstore(BookstoreId(bookstoreId)).map { item ->
                val book = bookService.getBookById(item.bookId)
                fromDomain(item, book, bookStore)
            }
        )
    }

    @PostMapping("/{bookstoreId}/book/{isbn}/reserve")
    override fun reserveBook(
        @PathVariable bookstoreId: UUID,
        @PathVariable isbn: String
    ): ResponseEntity<BooksReservationDto> {
        val book = bookService.getBookById(Book.BookId(isbn))
        val bookstore = bookstoreService.getBookstoreById(bookstoreId)

        // For now, creating a mock reservation - you'll need to implement actual reservation logic
        val reservationDto = BooksReservationDto(
            reservationId = UUID.randomUUID(),
            book = BookDto.fromDomain(book),
            bookstore = BookstoreDto.fromDomain(bookstore),
            reservedAt = java.time.LocalDateTime.now().toString(),
            status = "RESERVED"
        )

        return ResponseEntity.ok(reservationDto)
    }
}