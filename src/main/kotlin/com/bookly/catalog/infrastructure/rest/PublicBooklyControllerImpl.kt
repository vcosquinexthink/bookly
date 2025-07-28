package com.bookly.catalog.infrastructure.rest

import com.bookly.book.application.BookService
import com.bookly.book.domain.model.Book
import com.bookly.catalog.application.BookstoreService
import com.bookly.catalog.application.InventoryService
import com.bookly.catalog.domain.model.BookstoreId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/bookly/public")
class PublicBooklyControllerImpl(
    private val bookstoreService: BookstoreService,
    private val bookService: BookService,
    private val inventoryService: InventoryService
) : PublicBooklyController {

    @GetMapping("/inventory/search")
    override fun searchInventoryByIsbnAndLocation(
        @RequestParam isbn: String, @RequestParam location: Int
    ): ResponseEntity<List<InventoryItemDto>> {
        val book = bookService.getBookById(Book.BookId(isbn))
        return ResponseEntity.ok(
            bookstoreService.listBookstoresOrderedByProximity(location).flatMap { bookstore ->
                inventoryService.getInventoriesForBookstore(bookstore.id).filter { it.bookId == Book.BookId(isbn) }
                    .map { item -> InventoryItemDto.fromDomain(item, book, bookstore) }
            }
        )
    }

    @GetMapping("/bookstores/{bookstoreId}/catalog")
    override fun getBookstoreCatalog(
        @PathVariable bookstoreId: UUID
    ): ResponseEntity<List<InventoryItemDto>> {
        val bookStore = bookstoreService.getBookstoreById(bookstoreId)
        return ResponseEntity.ok(
            inventoryService.getInventoriesForBookstore(BookstoreId(bookstoreId)).map { item ->
                val book = bookService.getBookById(item.bookId)
                InventoryItemDto.fromDomain(item, book, bookStore)
            }
        )
    }
}
