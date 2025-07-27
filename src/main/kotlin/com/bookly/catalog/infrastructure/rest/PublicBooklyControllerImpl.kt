package com.bookly.catalog.infrastructure.rest

import com.bookly.catalog.application.BookService
import com.bookly.catalog.application.BookstoreService
import com.bookly.catalog.application.InventoryService
import com.bookly.catalog.domain.model.valueobject.BookId
import com.bookly.catalog.domain.model.valueobject.BookstoreId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/public")
class PublicBooklyControllerImpl(
    private val bookstoreService: BookstoreService,
    private val bookService: BookService,
    private val inventoryService: InventoryService
) : PublicBooklyController {

    @GetMapping("/inventory/search")
    override fun searchInventoryByIsbnAndLocation(
        @RequestParam isbn: String, @RequestParam location: Int
    ): ResponseEntity<List<InventoryItemDto>> {
        val book = bookService.getBookById(BookId(isbn))
        return ResponseEntity.ok(
            bookstoreService.listBookstoresOrderedByProximity(location).flatMap { bookstore ->
                inventoryService.getInventoriesForBookstore(bookstore.id).filter { it.bookId == BookId(isbn) }
                    .map { item -> InventoryItemDto.fromDomain(item, book, bookstore) }
            }
        )
    }

    @GetMapping("/bookstores/search")
    override fun searchBookstoresByProximity(
        @RequestParam location: Int
    ): ResponseEntity<List<BookstoreDto>> {
        return ResponseEntity.ok(
            bookstoreService.listBookstoresOrderedByProximity(location).map { BookstoreDto.fromDomain(it) }
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
