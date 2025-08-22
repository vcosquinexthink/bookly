package com.bookly.inventory.infrastructure.rest

import com.bookly.book.application.BookService
import com.bookly.book.domain.model.Book
import com.bookly.bookstore.application.BookstoreService
import com.bookly.inventory.application.InventoryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/bookly/inventory")
class InventoryControllerImpl(
    private val bookstoreService: BookstoreService,
    private val bookService: BookService,
    private val inventoryService: InventoryService
) : InventoryController {

    @GetMapping("/search")
    override fun searchInventoryByIsbnAndLocation(
        @RequestParam isbn: String, @RequestParam location: Int
    ): ResponseEntity<List<InventoryItemDto>> {
        val book = bookService.getBookById(Book.BookId(isbn))
        val orderedBookstores = bookstoreService.listBookstoresOrderedByProximity(location)
        val result = orderedBookstores.flatMap { bookstore ->
            inventoryService.getInventoriesForBookstoreAndBook(bookstore.id, book.getBookId())
                .filter { it.bookId == Book.BookId(isbn) }
                .map { item -> InventoryItemDto.fromDomain(item, book, bookstore) }
        }
        return ResponseEntity.ok(result)
    }
}
